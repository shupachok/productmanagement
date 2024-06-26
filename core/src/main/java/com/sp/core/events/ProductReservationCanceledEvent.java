package com.sp.core.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReservationCanceledEvent {
	private final String productId;
	private final String orderId;
	private final String userId;
	private final int quantity;
}
