package com.sp.core.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProductOrdered {
	private String productOrderedId;
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;
}
