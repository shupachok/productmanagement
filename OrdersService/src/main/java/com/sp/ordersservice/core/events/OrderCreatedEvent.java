package com.sp.ordersservice.core.events;

import java.util.List;

import com.sp.core.model.OrderStatus;
import com.sp.core.model.ProductOrdered;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
	private String orderId;
	private String userId;
	private String addressId;
	private OrderStatus orderStatus;
	List<ProductOrdered> productOrdered;

}
