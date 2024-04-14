package com.sp.ordersservice.command.rest;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CreateProductOrderedRestModel {
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;
}
