package com.sp.ordersservice.command.rest;

import lombok.Data;

@Data
public class CreateOrderRestModel {
	private String productId;
	private Integer quantity;
	private String addressId;
}
