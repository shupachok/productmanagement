package com.sp.core.commands;

import java.util.List;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.sp.core.model.ProductOrdered;
import com.sp.core.model.User;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SendEmailCommand {
	@TargetAggregateIdentifier
	private final String emailId;
	
	private final String orderId;
	
	private final User user;
	
	private final List<ProductOrdered> productOrdereds;

}
