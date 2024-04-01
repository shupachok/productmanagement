package com.sp.usersservice.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.sp.core.model.PaymentDetails;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateUserCommand {
	
	@TargetAggregateIdentifier
	private String userId;
	private String username;
	private String password;
	private String email;
	private String roleId;
	private String firstName;
	private String lastName;

	private PaymentDetails paymentDetails;

}
