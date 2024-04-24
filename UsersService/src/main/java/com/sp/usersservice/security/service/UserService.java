package com.sp.usersservice.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.sp.usersservice.core.data.UserDetailEntity;

public interface UserService extends UserDetailsService {
	UserDetailEntity getUserByEmail(String username);
}
