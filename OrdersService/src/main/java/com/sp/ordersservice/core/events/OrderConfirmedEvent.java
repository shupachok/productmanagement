package com.sp.ordersservice.core.events;

import java.util.List;

import com.sp.core.model.OrderStatus;
import com.sp.core.model.ProductOrdered;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderConfirmedEvent {
	private String orderId;
	private String userId;
	private OrderStatus orderStatus;
	List<ProductOrdered> productOrdered;
}
