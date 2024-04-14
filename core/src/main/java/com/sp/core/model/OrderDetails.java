package com.sp.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetails {
	private final String orderId;
	private final String productId;
	private final String userId;
	private final int quantity;
	private final String addressId;
}
