package com.sp.ordersservice.saga;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sp.core.commands.CancelProductReservationCommand;
import com.sp.core.commands.ConfirmOrderCommand;
import com.sp.core.commands.ProcessPaymentCommand;
import com.sp.core.commands.ReserveProductCommand;
import com.sp.core.commands.SendEmailCommand;
import com.sp.core.events.EmailDispatchedEvent;
import com.sp.core.events.OrderApprovedEvent;
import com.sp.core.events.PaymentProcessedEvent;
import com.sp.core.events.ProductReservationCanceledEvent;
import com.sp.core.model.OrderDetail;
import com.sp.core.model.OrderStatus;
import com.sp.core.model.OrderSummary;
import com.sp.core.model.ProductOrdered;
import com.sp.core.model.User;
import com.sp.core.query.FetchOrderDetailsQuery;
import com.sp.core.query.FetchUserPaymentDetailsQuery;
import com.sp.ordersservice.command.commands.ApproveOrderCommand;
import com.sp.ordersservice.command.commands.RejectOrderCommand;
import com.sp.ordersservice.core.events.OrderConfirmedEvent;
import com.sp.ordersservice.core.events.OrderCreatedEvent;
import com.sp.ordersservice.core.events.OrderRejectedEvent;
import com.sp.ordersservice.query.FindOrderSummaryQuery;

@Saga
public class OrderSaga {

	private final String PAYMENT_PROCESSING_DEADLINE = "payment-processing-deadline";

	@Autowired
	private transient CommandGateway commandGateway;
	
	@Autowired
	private transient QueryGateway queryGateway;
	
	@Autowired
	private transient DeadlineManager deadlineManager;
	
	@Autowired
	private transient QueryUpdateEmitter queryUpdateEmitter;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

	private String scheduleId = null; 

	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent orderCreatedEvent) {

		List<ProductOrdered> productOrdereds = orderCreatedEvent.getProductOrdered();
		
		List<ProductOrdered> rejectProductOrdereds = new ArrayList<>();
		
		for(int i=0;i<productOrdereds.size();i++) {
			ReserveProductCommand reserveProductCommand = ReserveProductCommand
			.builder()
			.orderId(orderCreatedEvent.getOrderId())
			.userId(orderCreatedEvent.getUserId())
			.productId(productOrdereds.get(i).getProductId())
			.title(productOrdereds.get(i).getTitle())
			.price(productOrdereds.get(i).getPrice())
			.quantity(productOrdereds.get(i).getQuantity())
			.build();
			
			
			try {
				commandGateway.sendAndWait(reserveProductCommand);
			} catch (Exception e) {
				cancelProductReservation(rejectProductOrdereds,orderCreatedEvent.getOrderId(),e.getMessage());
				
				LOGGER.error("error: "+e.getMessage());
				return;
			}
			
			rejectProductOrdereds.add(productOrdereds.get(i));
		}
		
		ConfirmOrderCommand confirmOrderCommand = ConfirmOrderCommand
		.builder()
		.orderId(orderCreatedEvent.getOrderId())
		.orderStatus(OrderStatus.CREATED)
		.userId(orderCreatedEvent.getUserId())
		.productOrdered(orderCreatedEvent.getProductOrdered())
		.build();
		
		try {
			commandGateway.sendAndWait(confirmOrderCommand);
			
		} catch (Exception e) {
			cancelProductReservation(rejectProductOrdereds,orderCreatedEvent.getOrderId(),e.getMessage());
			
			LOGGER.error("error: "+e.getMessage());
			return;
		}
	}
	
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderConfirmedEvent orderConfirmedEvent) {		
		FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = 
				new FetchUserPaymentDetailsQuery(orderConfirmedEvent.getUserId());
		User user;
		
		try {
			user = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			
			cancelProductReservation(orderConfirmedEvent.getProductOrdered(),orderConfirmedEvent.getOrderId(),e.getMessage());
			return;
		}
		
		if(user == null) {
			//start compensating transaction
			cancelProductReservation(orderConfirmedEvent.getProductOrdered(),orderConfirmedEvent.getOrderId(),"Could not fetch user detail");
			
			return;
		}
		
		LOGGER.info("Successful fetch user payment detail user:"+user.getFirstName());
		
		scheduleId = deadlineManager.schedule(Duration.of(1, ChronoUnit.DAYS),
				PAYMENT_PROCESSING_DEADLINE, orderConfirmedEvent);
		
//		if(true) return; //test schedule
		
		ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand
		.builder()
		.paymentId(UUID.randomUUID().toString())
		.orderId(orderConfirmedEvent.getOrderId())
		.paymentDetails(user.getPaymentDetails()).build();
		
		String result = null;
		try {
			result = commandGateway.sendAndWait(processPaymentCommand);
		} catch (Exception e) {
			//start compensating transaction
			LOGGER.error(e.getMessage());
			
			cancelProductReservation(orderConfirmedEvent.getProductOrdered(),orderConfirmedEvent.getOrderId(),e.getMessage());
			
			return;
		}
		
		if(result == null) {
			//start compensating transaction
			LOGGER.info("Could not process user payment with user:"+user.getFirstName());
			cancelProductReservation(orderConfirmedEvent.getProductOrdered(),orderConfirmedEvent.getOrderId(),"Could not process user payment with user:"+user.getFirstName());
		}
	}
	
	private void cancelProductReservation(List<ProductOrdered> rejectProductOrdereds,String orderId,String reason) {
				
		for(ProductOrdered rejectProductOrdered:rejectProductOrdereds) {
			CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand
					.builder()
					.productId(rejectProductOrdered.getProductId())
					.orderId(orderId)
					.quantity(rejectProductOrdered.getQuantity())
					.reason(reason)
					.build();
		
			commandGateway.sendAndWait(cancelProductReservationCommand);
		}
		
		RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderId
				, reason,new ArrayList<ProductOrdered>());
		commandGateway.send(rejectOrderCommand);
	
		
	}


	private void cancelDeadline() {
		if(scheduleId != null) {
			deadlineManager.cancelSchedule(PAYMENT_PROCESSING_DEADLINE, scheduleId);
			scheduleId = null;
		}
	} 
	
//	private void cancelDeadline() {
//		deadlineManager.cancelAll(PAYMENT_PROCESSING_DEADLINE);
//	} 
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessedEvent paymentProcessedEvent) {
		cancelDeadline();
		
		//approve order
		ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
		commandGateway.send(approveOrderCommand);
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent orderApprovedEvent) {
		LOGGER.info("SAGA is complete to approved orderId:"+orderApprovedEvent.getOrderId());
//		SagaLifecycle.end(); //use this method or use @EndSaga
		
		FetchOrderDetailsQuery fetchOrderDetailsQuery = new FetchOrderDetailsQuery(orderApprovedEvent.getOrderId());
		
		OrderDetail orderDetail = queryGateway.query(fetchOrderDetailsQuery, ResponseTypes.instanceOf(OrderDetail.class)).join();
		
		String userId = orderDetail.getUserId();
		 
		FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = 
					new FetchUserPaymentDetailsQuery(userId);
		User user = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
		
		SendEmailCommand sendEmailCommand = SendEmailCommand.builder()
		.emailId(UUID.randomUUID().toString())
		.orderId(orderDetail.getOrderId())
		.user(user)
		.productOrdereds(orderDetail.getProductOrdereds())
		.build();
		
		commandGateway.send(sendEmailCommand);
		
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(EmailDispatchedEvent emailDispatchedEvent) {
		LOGGER.info("SAGA is complete to send email for orderId:"+emailDispatchedEvent.getOrderId());
		
		queryUpdateEmitter.emit(FindOrderSummaryQuery.class
				, query -> true
				,new OrderSummary(emailDispatchedEvent.getOrderId()
						,OrderStatus.APPROVED
						,""));
	}
	
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservationCanceledEvent productReservationCanceledEvent) {
		//edit array prodeuct ordered
		
		String reason="";
		RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCanceledEvent.getOrderId()
				,reason ,new ArrayList<ProductOrdered>());
		commandGateway.send(rejectOrderCommand);
		
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderRejectedEvent orderRejectedEvent) {
		LOGGER.info("Order is rejected with id:"+orderRejectedEvent.getOrderId());
		queryUpdateEmitter.emit(FindOrderSummaryQuery.class
				, query -> true
				,new OrderSummary(orderRejectedEvent.getOrderId()
						,orderRejectedEvent.getOrderStatus()
						,orderRejectedEvent.getReason()));
	}
	
	@DeadlineHandler(deadlineName = PAYMENT_PROCESSING_DEADLINE)
	public void handlePaymentDeadline(OrderConfirmedEvent orderConfirmedEvent) {
		//ProductReservedEvent must be set on payload parameter when schedule
		LOGGER.info("Payment Timeout. Start compensating command");
		cancelProductReservation(orderConfirmedEvent.getProductOrdered(),orderConfirmedEvent.getOrderId(),"Payment Timeout");
		
	}
}
