package com.sp.ordersservice.core.data;

import java.util.List;

import com.sp.core.model.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class OrderEntity {
	@Id
	@Column(unique = true)
	public String orderId;
	private String userId;
	private String addressId;
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	
	@OneToMany(mappedBy = "order")
	private List<ProductOrderedEntity> productOrdereds;
}

