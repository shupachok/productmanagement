package com.sp.usersservice.query;

import java.util.Arrays;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sp.core.model.PaymentDetails;
import com.sp.usersservice.Roles;
import com.sp.usersservice.core.data.PaymentDetailEntity;
import com.sp.usersservice.core.data.PaymentDetailRepository;
import com.sp.usersservice.core.data.RoleEntity;
import com.sp.usersservice.core.data.RolesRepository;
import com.sp.usersservice.core.data.UserDetailEntity;
import com.sp.usersservice.core.data.UserDetailsRepository;
import com.sp.usersservice.core.event.UserCreatedEvent;

@Component
public class UserEventsHandler {

	private final UserDetailsRepository userDetailsRepository;

	private final PaymentDetailRepository paymentDetailRepository;

	private final RolesRepository rolesRepository;

	public UserEventsHandler(UserDetailsRepository userDetailsRepository,
			PaymentDetailRepository paymentDetailRepository, RolesRepository rolesRepository) {
		this.userDetailsRepository = userDetailsRepository;
		this.paymentDetailRepository = paymentDetailRepository;
		this.rolesRepository = rolesRepository;
	}

	@EventHandler
	public void handler(UserCreatedEvent userCreatedEvent) {
		UserDetailEntity userDetailEntity = new UserDetailEntity();
		userDetailEntity.setId(userCreatedEvent.getUserId());
		userDetailEntity.setUsername(userCreatedEvent.getUsername());
		userDetailEntity.setPassword(userCreatedEvent.getPassword());
		userDetailEntity.setEmail(userCreatedEvent.getEmail());
		userDetailEntity.setFirstName(userCreatedEvent.getFirstName());
		userDetailEntity.setLastName(userCreatedEvent.getLastName());

		PaymentDetailEntity paymentDetailEntity = new PaymentDetailEntity();

		PaymentDetails paymentDetails = userCreatedEvent.getPaymentDetails();
		BeanUtils.copyProperties(paymentDetails, paymentDetailEntity);

		RoleEntity roleUserEntity = rolesRepository.findByName(Roles.ROLE_USER.name());
		
		userDetailEntity.setRoles(Arrays.asList(roleUserEntity));
		
		userDetailsRepository.save(userDetailEntity);

		paymentDetailEntity.setPaymentId(paymentDetails.getPaymentId());
		paymentDetailEntity.setUserDetailEntity(userDetailEntity);
		paymentDetailRepository.save(paymentDetailEntity);

	}
}
