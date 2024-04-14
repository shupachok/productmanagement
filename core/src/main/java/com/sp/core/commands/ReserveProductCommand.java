package com.sp.core.commands;

import java.math.BigDecimal;

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
	private final String title;
	private final BigDecimal price;
	private final int quantity;
	
}
