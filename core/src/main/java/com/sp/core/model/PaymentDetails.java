package com.sp.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDetails {

	private final String paymentId;
	private final String name;
	private final String cardNumber;
	private final int validUntilMonth;
	private final int validUntilYear;
	private final String cvv;
	
}
