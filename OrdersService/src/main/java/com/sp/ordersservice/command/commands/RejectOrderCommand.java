package com.sp.ordersservice.command.commands;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.sp.core.model.ProductOrdered;

import lombok.Value;

@Value
public class RejectOrderCommand {

	@TargetAggregateIdentifier
	private final String orderId;
	private final String reason;
	List<ProductOrdered> productOrdered;
}
