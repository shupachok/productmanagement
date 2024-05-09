package com.sp.productmanagement.core.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreatedEvent {
	private String productId;
	private String title;
	private String pathImage;
	private BigDecimal price;
	private Integer quantity;
}
