package com.sp.usersservice.query.rest;

import lombok.Data;

@Data
public class UserRestModel {
	private String userId;
	
	private String username;
	private String password;
	private String email;
	
	private String firstName;
	private String lastName;
	
	private PaymentDetailRestModel paymentDetail;
	private RoleRestModel role;

}
