package com.sp.core.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReservedEvent {
	private String orderId;
	private String userId;
	private String productId;
	private int quantity;
}
