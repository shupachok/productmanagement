package com.sp.paymentsservice.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.sp.core.commands.ProcessPaymentCommand;
import com.sp.core.events.PaymentProcessedEvent;

@Aggregate
public class PaymentAggregate {
	
	@AggregateIdentifier
	private String paymentId;
	private String orderId;
	public PaymentAggregate() {
	}
	
	@CommandHandler
	public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
		//validate payment
		if(processPaymentCommand.getOrderId() == null)
			throw new IllegalArgumentException("Order ID cannnot be null");
		if(processPaymentCommand.getPaymentDetails().getCardNumber() == null ||
				processPaymentCommand.getPaymentDetails().getCardNumber().isBlank() == true)
			throw new IllegalArgumentException("Card Number cannnot be blank");
		PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent
				.builder()
				.paymentId(processPaymentCommand.getPaymentId())
				.orderId(processPaymentCommand.getOrderId())
				.build();
		
		AggregateLifecycle.apply(paymentProcessedEvent);
		
		
	}
	
	@EventSourcingHandler
	public void on(PaymentProcessedEvent paymentProcessedEvent) {
		this.paymentId = paymentProcessedEvent.getPaymentId();
		this.orderId = paymentProcessedEvent.getOrderId();
	}
	
	
}
