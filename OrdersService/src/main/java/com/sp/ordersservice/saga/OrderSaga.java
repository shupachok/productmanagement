package com.sp.ordersservice.saga;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
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
import com.sp.core.commands.ProcessPaymentCommand;
import com.sp.core.commands.ReserveProductCommand;
import com.sp.core.events.OrderApprovedEvent;
import com.sp.core.events.PaymentProcessedEvent;
import com.sp.core.events.ProductReservationCanceledEvent;
import com.sp.core.events.ProductReservedEvent;
import com.sp.core.model.OrderSummary;
import com.sp.core.model.User;
import com.sp.core.query.FetchUserPaymentDetailsQuery;
import com.sp.ordersservice.command.commands.ApproveOrderCommand;
import com.sp.ordersservice.command.commands.RejectOrderCommand;
import com.sp.ordersservice.core.events.OrderCreatedEvent;
import com.sp.ordersservice.core.events.OrderRejectedEvent;
import com.sp.ordersservice.query.FindOrderQuery;

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

		ReserveProductCommand reserveProductCommand = ReserveProductCommand
				.builder()
				.orderId(orderCreatedEvent.getOrderId())
				.userId(orderCreatedEvent.getUserId())
				.productId(orderCreatedEvent.getProductId())
				.quantity(orderCreatedEvent.getQuantity())
				.build();

		LOGGER.info("OrderCreatedEvent handled for event = " + reserveProductCommand.getOrderId()+
				" and productId = "+reserveProductCommand.getProductId());
		
		commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
					CommandResultMessage<? extends Object> commandResultMessage) {
				if (commandResultMessage.isExceptional()) {
					// Start a compensating transaction
					// roll back when exception
					RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderCreatedEvent.getOrderId()
							, commandResultMessage.exceptionResult().getMessage());
					commandGateway.send(rejectOrderCommand);
					
				}

			}

		});
	}
	
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservedEvent productReservedEvent) {
		//Process user payment

		LOGGER.info("ProductReservedEvent handled for event = " + productReservedEvent.getOrderId()+
				" and productId = "+productReservedEvent.getProductId());
		
		FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = 
				new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
		User user;
		
		try {
			user = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			
			cancelProductReservation(productReservedEvent,e.getMessage());
			return;
		}
		
		if(user == null) {
			//start compensating transaction
			cancelProductReservation(productReservedEvent,"Could not fetch user detail");
			return;
		}
		
		LOGGER.info("Successful fetch user payment detail user:"+user.getFirstName());
		
		scheduleId = deadlineManager.schedule(Duration.of(30, ChronoUnit.SECONDS),
				PAYMENT_PROCESSING_DEADLINE, productReservedEvent);
		
//		if(true) return; //test schedule
		
		ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand
		.builder()
		.paymentId(UUID.randomUUID().toString())
		.orderId(productReservedEvent.getOrderId())
		.paymentDetails(user.getPaymentDetails()).build();
		
		String result = null;
		try {
			result = commandGateway.sendAndWait(processPaymentCommand);
		} catch (Exception e) {
			//start compensating transaction
			LOGGER.error(e.getMessage());
			
			cancelProductReservation(productReservedEvent,e.getMessage());
			return;
		}
		
		if(result == null) {
			//start compensating transaction
			LOGGER.info("Could not process user payment with user:"+user.getFirstName());
			cancelProductReservation(productReservedEvent,"Could not process user payment with user:"+user.getFirstName());
		}
	}
	
	private void cancelProductReservation(ProductReservedEvent productReservedEvent,String reason) {
		cancelDeadline();
		
		CancelProductReservationCommand cancelProductReservationCommand = CancelProductReservationCommand
		.builder()
		.productId(productReservedEvent.getProductId())
		.orderId(productReservedEvent.getOrderId())
		.quantity(productReservedEvent.getQuantity())
		.reason(reason)
		.build();
		
		commandGateway.send(cancelProductReservationCommand);
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
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent orderApprovedEvent) {
		LOGGER.info("SAGA is complete to approved orderId:"+orderApprovedEvent.getOrderId());
//		SagaLifecycle.end(); //use this method or use @EndSaga
		queryUpdateEmitter.emit(FindOrderQuery.class
				, query -> true
				,new OrderSummary(orderApprovedEvent.getOrderId()
						,orderApprovedEvent.getOrderStatus()
						,""));
	}
	
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservationCanceledEvent productReservationCanceledEvent) {
		RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCanceledEvent.getOrderId(), productReservationCanceledEvent.getReason());
		commandGateway.send(rejectOrderCommand);
		
	}
	
	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderRejectedEvent orderRejectedEvent) {
		LOGGER.info("Order is rejected with id:"+orderRejectedEvent.getOrderId());
		queryUpdateEmitter.emit(FindOrderQuery.class
				, query -> true
				,new OrderSummary(orderRejectedEvent.getOrderId()
						,orderRejectedEvent.getOrderStatus()
						,orderRejectedEvent.getReason()));
	}
	
	@DeadlineHandler(deadlineName = PAYMENT_PROCESSING_DEADLINE)
	public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
		//ProductReservedEvent must be set on payload parameter when schedule
		LOGGER.info("Payment Timeout. Start compensating command");
		cancelProductReservation(productReservedEvent,"Payment Timeout");
		
	}
}
