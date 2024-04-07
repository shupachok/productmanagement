package com.sp.usersservice.core.event;

import com.sp.core.model.PaymentDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedEvent {
	private String userId;
	private String username;
	private String password;
	private String email;
	private String roleId;
	private String firstName;
	private String lastName;
	private PaymentDetails paymentDetails;

}
