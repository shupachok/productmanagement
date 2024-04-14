package com.sp.ordersservice.command.rest;

import java.util.List;

import lombok.Data;

@Data
public class CreateOrderRestModel {
	private String userId;
	private String addressId;
	
	List<CreateProductOrderedRestModel> productOrdereds;
	
}
