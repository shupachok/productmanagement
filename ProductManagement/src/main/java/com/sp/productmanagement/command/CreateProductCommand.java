package com.sp.productmanagement.command;

import java.math.BigDecimal;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateProductCommand {
	
	@TargetAggregateIdentifier
	private final String productId;
	private final String title;
	private final String pathImage;
	private final BigDecimal price;
	private final Integer quantity;
}
