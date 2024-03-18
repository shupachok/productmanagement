package com.sp.ordersservice.core.events;

import com.sp.core.model.OrderStatus;

import lombok.Value;

@Value
public class OrderRejectedEvent {
	private final String orderId;
	private final OrderStatus orderStatus = OrderStatus.REJECTED;
	private final String reason;
}
