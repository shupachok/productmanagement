package com.sp.core.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReservedEvent {
	private String orderId;
	private String userId;
	private String productId;
	private int quantity;
}
