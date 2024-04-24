package com.sp.usersservice.security;

import lombok.Data;

@Data
public class LoginRequestModel {
	private String email;
	private String password;
}
