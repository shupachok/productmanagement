package com.sp.emailservice.command.rest;

import java.util.List;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.sp.core.commands.SendEmailCommand;
import com.sp.core.events.EmailDispatchedEvent;
import com.sp.core.model.ProductOrdered;
import com.sp.core.model.User;

@Aggregate
public class EmailAggregate {

	@AggregateIdentifier
	private String emailId;
	private String orderId;
	private User user;
	private List<ProductOrdered> productOrdereds;
	
	public EmailAggregate() {
	}
	
	@CommandHandler
	public EmailAggregate(SendEmailCommand sendEmailCommand) {
		EmailDispatchedEvent emailDispatchedEvent = EmailDispatchedEvent.builder()
			.emailId(sendEmailCommand.getEmailId())
			.orderId(sendEmailCommand.getOrderId())
			.user(sendEmailCommand.getUser())
			.productOrdereds(sendEmailCommand.getProductOrdereds())
			.build();
		
		AggregateLifecycle.apply(emailDispatchedEvent);
		
	}
	
	@EventSourcingHandler
	public void on(EmailDispatchedEvent emailDispatchedEvent) {
		emailId = emailDispatchedEvent.getEmailId();
		orderId = emailDispatchedEvent.getOrderId();
		user = emailDispatchedEvent.getUser();
		productOrdereds = emailDispatchedEvent.getProductOrdereds();
	}
	
	

}
