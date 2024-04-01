package com.sp.usersservice.query;

import java.util.Optional;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.sp.core.model.PaymentDetails;
import com.sp.core.model.User;
import com.sp.core.query.FetchUserPaymentDetailsQuery;
import com.sp.usersservice.core.data.PaymentDetailEntity;
import com.sp.usersservice.core.data.UserDetailEntity;
import com.sp.usersservice.core.data.UserDetailsRepository;

@Component
public class UsersQueryHandler {

	UserDetailsRepository userDetailsRepository;

	public UsersQueryHandler(UserDetailsRepository userDetailsRepository) {
		super();
		this.userDetailsRepository = userDetailsRepository;
	}

	@QueryHandler
	public User findUser(FetchUserPaymentDetailsQuery query) {

		Optional<UserDetailEntity> userOptional = userDetailsRepository.findById(query.getUserId());

		if (userOptional.isPresent()) {
			UserDetailEntity userDetailEntity = userOptional.get();

			PaymentDetailEntity paymentDetailEntity = userDetailEntity.getPaymentDetailEntity();

			PaymentDetails paymentDetails = PaymentDetails.builder()
					.paymentId(paymentDetailEntity.getPaymentId())
					.name(paymentDetailEntity.getName())
					.cardNumber(paymentDetailEntity.getCardNumber())
					.cvv(paymentDetailEntity.getCvv())
					.validUntilMonth(paymentDetailEntity.getValidUntilMonth())
					.validUntilYear(paymentDetailEntity.getValidUntilYear()).build();
			
			User user = User.builder()
					.userId(userDetailEntity.getUserId())
					.username(userDetailEntity.getUsername())
					.password(userDetailEntity.getPassword())
					.email(userDetailEntity.getEmail())
					.firstName(userDetailEntity.getFirstName())
					.lastName(userDetailEntity.getLastName())
					.paymentDetails(paymentDetails)
					.build();
			
			return user;

		}

		return null;
	}
}
