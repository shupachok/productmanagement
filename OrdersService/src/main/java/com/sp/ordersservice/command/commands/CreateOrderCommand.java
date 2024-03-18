package com.sp.ordersservice.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.sp.core.model.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateOrderCommand {
	
	@TargetAggregateIdentifier
	public final String orderId;
	private final String userId;
	private final String productId;
	private final int quantity;
	private final String addressId;
	private final OrderStatus orderStatus;

}
