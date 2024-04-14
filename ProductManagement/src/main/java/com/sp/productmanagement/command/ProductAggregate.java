package com.sp.productmanagement.command;

import java.math.BigDecimal;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.sp.core.commands.CancelProductReservationCommand;
import com.sp.core.commands.ReserveProductCommand;
import com.sp.core.events.ProductReservationCanceledEvent;
import com.sp.core.events.ProductReservedEvent;
import com.sp.productmanagement.core.event.ProductCreatedEvent;

@Aggregate(snapshotTriggerDefinition = "productSnapshotTriggerDefinition")
public class ProductAggregate {

	@AggregateIdentifier
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;
	
	public ProductAggregate() {
		
	}
	
	@CommandHandler
	public ProductAggregate(CreateProductCommand createProductCommand) {
		//validate product aggregate
		if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <=0)
			throw new IllegalArgumentException("Price cannnot be less than or equal zero");
		if(createProductCommand.getTitle() == null 
				|| createProductCommand.getTitle().isBlank())
			throw new IllegalArgumentException("Title cannnot be empty");
		
		ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
		BeanUtils.copyProperties(createProductCommand, productCreatedEvent);
		AggregateLifecycle.apply(productCreatedEvent);
		
	}
	
	@CommandHandler
	public void handle(ReserveProductCommand reserveProductCommand) {
		if(!title.equals(reserveProductCommand.getTitle())) {
			throw new IllegalArgumentException(String.format("Title in database does not match submitted title (Product Id: %s)",productId));
		}
		
		if(!price.equals(reserveProductCommand.getPrice())) {
			throw new IllegalArgumentException(String.format("Price in database does not match submitted price (Product Id: %s)",productId));
		}
		
		if(quantity < reserveProductCommand.getQuantity()) {
			throw new IllegalArgumentException(String.format("Insufficient number of Item in stock (Product Id: %s)",productId));
		}
		
		ProductReservedEvent productReservedEvent = ProductReservedEvent
		.builder()
		.orderId(reserveProductCommand.getOrderId())
		.userId(reserveProductCommand.getUserId())
		.productId(reserveProductCommand.getProductId())
		.quantity(reserveProductCommand.getQuantity())
		.build();
		
		AggregateLifecycle.apply(productReservedEvent);
		
	}
	
	@CommandHandler
	public void handle(CancelProductReservationCommand cancelProductReservationCommand) {
		ProductReservationCanceledEvent productReservationCanceledEvent = ProductReservationCanceledEvent
		.builder()
		.productId(cancelProductReservationCommand.getProductId())
		.orderId(cancelProductReservationCommand.getOrderId())
		.userId(cancelProductReservationCommand.getUserId())
		.quantity(cancelProductReservationCommand.getQuantity())
		.build();
		
		AggregateLifecycle.apply(productReservationCanceledEvent);
	}
	
	@EventSourcingHandler
	public void on(CancelProductReservationCommand cancelProductReservationCommand) {
		this.quantity += cancelProductReservationCommand.getQuantity();
	}
	
	@EventSourcingHandler
	public void on(ProductCreatedEvent productCreatedEvent) {
		this.productId = productCreatedEvent.getProductId();
		this.price = productCreatedEvent.getPrice();
		this.title = productCreatedEvent.getTitle();
		this.quantity = productCreatedEvent.getQuantity();
	}
	
	@EventSourcingHandler
	public void on(ProductReservedEvent productReservedEvent) {
		this.quantity -= productReservedEvent.getQuantity();
	}
}
