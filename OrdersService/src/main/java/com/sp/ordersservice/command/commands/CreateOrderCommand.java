package com.sp.ordersservice.command.commands;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.sp.core.model.OrderStatus;
import com.sp.core.model.ProductOrdered;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateOrderCommand {
	
	@TargetAggregateIdentifier
	public final String orderId;
	private final String userId;
	private final String addressId;
	private final OrderStatus orderStatus;
	private final List<ProductOrdered> productOrdered;

}
