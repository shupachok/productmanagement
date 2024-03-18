package com.sp.ordersservice.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.sp.core.events.OrderApprovedEvent;
import com.sp.core.model.OrderStatus;
import com.sp.ordersservice.command.commands.ApproveOrderCommand;
import com.sp.ordersservice.command.commands.CreateOrderCommand;
import com.sp.ordersservice.command.commands.RejectOrderCommand;
import com.sp.ordersservice.core.events.OrderCreatedEvent;
import com.sp.ordersservice.core.events.OrderRejectedEvent;

@Aggregate
public class OrderAggregate {

	@AggregateIdentifier
	private String orderId;
	private String userId;
	private String productId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;

	public OrderAggregate() {
	}
	
	@CommandHandler
	public OrderAggregate(CreateOrderCommand command) {
		
		OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
		BeanUtils.copyProperties(command, orderCreatedEvent);
		AggregateLifecycle.apply(orderCreatedEvent);
	}
	
	@EventSourcingHandler
	protected void on(OrderCreatedEvent orderCreatedEvent) {
		this.orderId = orderCreatedEvent.getOrderId();
		this.userId = orderCreatedEvent.getUserId();
		this.productId = orderCreatedEvent.getProductId();
		this.quantity = orderCreatedEvent.getQuantity();
		this.addressId = orderCreatedEvent.getAddressId();
		this.orderStatus = orderCreatedEvent.getOrderStatus();
	}
	
	@CommandHandler
	public void handle(ApproveOrderCommand approveOrderCommand) {
		OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approveOrderCommand.getOrderId());
		AggregateLifecycle.apply(orderApprovedEvent);
	}
	
	@EventSourcingHandler
	protected void on(OrderApprovedEvent orderApprovedEvent) {
		this.orderStatus = orderApprovedEvent.getOrderStatus();
	}

	@CommandHandler
	public void handle(RejectOrderCommand rejectOrderCommand) {
		OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(rejectOrderCommand.getOrderId()
				,rejectOrderCommand.getReason());
		AggregateLifecycle.apply(orderRejectedEvent);
	}
	
	@EventSourcingHandler
	protected void on(OrderRejectedEvent orderRejectedEvent) {
		this.orderStatus = orderRejectedEvent.getOrderStatus();
	}
}
