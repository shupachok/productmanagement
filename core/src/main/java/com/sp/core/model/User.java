package com.sp.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
	private final String userId;
	private final String username;
	private final String password;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final PaymentDetails paymentDetails;

}
