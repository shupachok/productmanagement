package com.sp.ordersservice.command.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApproveOrderCommand {
	
	@TargetAggregateIdentifier
	public final String orderId;
}
