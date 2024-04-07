package com.sp.usersservice.query.rest;

import lombok.Data;

@Data
public class PaymentDetailRestModel {
	private String paymentId;
	private String name;
	private String cardNumber;
	private int validUntilMonth;
	private int validUntilYear;
	private String cvv;
}
