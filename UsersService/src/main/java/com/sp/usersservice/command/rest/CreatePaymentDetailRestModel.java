package com.sp.usersservice.command.rest;

import lombok.Data;

@Data
public class CreatePaymentDetailRestModel {
	private String name;
	private String cardNumber;
	private int validUntilMonth;
	private int validUntilYear;
	private String cvv;

}
