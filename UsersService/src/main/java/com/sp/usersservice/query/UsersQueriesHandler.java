package com.sp.usersservice.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sp.core.model.PaymentDetails;
import com.sp.core.model.User;
import com.sp.core.query.FetchUserPaymentDetailsQuery;
import com.sp.usersservice.core.data.PaymentDetailEntity;
import com.sp.usersservice.core.data.RoleEntity;
import com.sp.usersservice.core.data.UserDetailEntity;
import com.sp.usersservice.core.data.UserDetailsRepository;
import com.sp.usersservice.query.rest.PaymentDetailRestModel;
import com.sp.usersservice.query.rest.RoleRestModel;
import com.sp.usersservice.query.rest.UserRestModel;

import ch.qos.logback.core.joran.util.beans.BeanUtil;

@Component
public class UsersQueriesHandler {

	UserDetailsRepository userDetailsRepository;

	public UsersQueriesHandler(UserDetailsRepository userDetailsRepository) {
		super();
		this.userDetailsRepository = userDetailsRepository;
	}

	@QueryHandler
	public User findUser(FetchUserPaymentDetailsQuery query) {

		Optional<UserDetailEntity> userOptional = userDetailsRepository.findById(query.getUserId());

		if (userOptional.isPresent()) {
			UserDetailEntity userDetailEntity = userOptional.get();

			PaymentDetailEntity paymentDetailEntity = userDetailEntity.getPaymentDetail();

			PaymentDetails paymentDetails = PaymentDetails.builder().paymentId(paymentDetailEntity.getPaymentId())
					.name(paymentDetailEntity.getName()).cardNumber(paymentDetailEntity.getCardNumber())
					.cvv(paymentDetailEntity.getCvv()).validUntilMonth(paymentDetailEntity.getValidUntilMonth())
					.validUntilYear(paymentDetailEntity.getValidUntilYear()).build();

			User user = User.builder().userId(userDetailEntity.getId()).username(userDetailEntity.getUsername())
					.password(userDetailEntity.getPassword()).email(userDetailEntity.getEmail())
					.firstName(userDetailEntity.getFirstName()).lastName(userDetailEntity.getLastName())
					.paymentDetails(paymentDetails).build();

			return user;

		}

		return null;
	}

	@QueryHandler
	public List<UserRestModel> findUser(FindUsersQuery findUsersQuery) {
		List<UserDetailEntity> userEntities = userDetailsRepository.findAll();
		List<UserRestModel> users = new ArrayList<>();

		for (UserDetailEntity userEntity : userEntities) {
			UserRestModel userModel = new UserRestModel();
			BeanUtils.copyProperties(userEntity, userModel);
			
			userModel.setUserId(userEntity.getId());
			
			PaymentDetailRestModel paymentModel = new PaymentDetailRestModel();
			if(userEntity.getPaymentDetail() != null)
				BeanUtils.copyProperties(userEntity.getPaymentDetail(),paymentModel);
			
			List<RoleEntity> roleEntities = userEntity.getRoles();
			
			List<RoleRestModel> roleRestModels = new ArrayList<>();
			
			for(RoleEntity roleEntitie:roleEntities) {
				RoleRestModel roleRestModel = new RoleRestModel();
				roleRestModel.setId(roleEntitie.getId());
				roleRestModel.setName(roleEntitie.getName());
				
				roleRestModels.add(roleRestModel);
			}
			
			userModel.setPaymentDetail(paymentModel);
			userModel.setRole(roleRestModels);
			
			users.add(userModel);
		}

		return users;
	}
}
