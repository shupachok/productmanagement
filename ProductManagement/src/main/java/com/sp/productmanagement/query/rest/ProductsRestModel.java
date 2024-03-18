package com.sp.productmanagement.query.rest;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductsRestModel {
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;
}
