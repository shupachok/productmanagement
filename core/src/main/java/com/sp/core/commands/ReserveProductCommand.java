package com.sp.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ReserveProductCommand {
	@TargetAggregateIdentifier
	private final String productId;
	private final String orderId;
	private final String userId;
	private final int quantity;
	
}
