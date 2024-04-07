package com.sp.usersservice.query.rest;

import java.util.List;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sp.usersservice.query.FindUsersQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/users")
public class UsersQueryController {
	
	@Autowired
	QueryGateway queryGateway;

	@GetMapping
	public List<UserRestModel> getUser() {

		FindUsersQuery findUsersQuery = new FindUsersQuery();
		List<UserRestModel> users = queryGateway.query(findUsersQuery, 
				ResponseTypes.multipleInstancesOf(UserRestModel.class)).join();
		return users;
	}
}
