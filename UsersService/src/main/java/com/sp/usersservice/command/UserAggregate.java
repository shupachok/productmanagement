package com.sp.usersservice.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.sp.core.model.PaymentDetails;
import com.sp.usersservice.command.commands.CreateUserCommand;
import com.sp.usersservice.core.event.UserCreatedEvent;

@Aggregate
public class UserAggregate {
	@AggregateIdentifier
	private String userId;
	private String username;
	private String password;
	private String email;
	private String roleId;
	private String firstName;
	private String lastName;
	private PaymentDetails paymentDetails;
	public UserAggregate() {
	}
	
	@CommandHandler
	public UserAggregate(CreateUserCommand createUserCommand) {
		UserCreatedEvent userCreatedEvent = new UserCreatedEvent();
		BeanUtils.copyProperties(createUserCommand,userCreatedEvent);
		AggregateLifecycle.apply(userCreatedEvent);
	}
	
	@EventSourcingHandler
	public void on(UserCreatedEvent userCreatedEvent) {
		userId = userCreatedEvent.getUserId();
		username = userCreatedEvent.getUsername();
		password = userCreatedEvent.getPassword();
		email = userCreatedEvent.getEmail();
		roleId = userCreatedEvent.getRoleId();
		firstName = userCreatedEvent.getFirstName();
		lastName = userCreatedEvent.getLastName();
		paymentDetails = userCreatedEvent.getPaymentDetails();
	}
	
	
	
	
	
}
