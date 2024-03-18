package com.sp.core.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.sp.core.model.PaymentDetails;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProcessPaymentCommand {
	
	@TargetAggregateIdentifier
	private String paymentId;
	private String orderId;
	private PaymentDetails paymentDetails;

}
