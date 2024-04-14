package com.sp.ordersservice.core.data;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "productordereds")
@Data
public class ProductOrderedEntity {
	@Id
	@Column(unique = true)
	public String productOrderedId;
	private String productId;
	private String title;
	private BigDecimal price;

	private Integer quantity;

	@ManyToOne
	private OrderEntity order;

}
