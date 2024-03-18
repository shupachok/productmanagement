package com.sp.usersservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.sp.core.model.PaymentDetails;
import com.sp.core.model.User;
import com.sp.core.query.FetchUserPaymentDetailsQuery;

@Component
public class UsersQueryHandler {

	@QueryHandler
	public User findUser(FetchUserPaymentDetailsQuery query) {
		PaymentDetails paymentDetails = PaymentDetails.builder().cardNumber("123Card").cvv("123")
				.name("John Sena").validUntilMonth(12).validUntilYear(2030).build();

		User userRest = User.builder().firstName("John").lastName("Sena").userId(query.getUserId())
				.paymentDetails(paymentDetails).build();

		return userRest;
	}
}
