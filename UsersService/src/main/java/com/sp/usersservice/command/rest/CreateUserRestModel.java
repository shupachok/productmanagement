package com.sp.usersservice.command.rest;

import lombok.Data;

@Data
public class CreateUserRestModel {
	private final String userId;
	private final String username;
	private final String password;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final CreatePaymentDetailRestModel paymentDetails;
}
