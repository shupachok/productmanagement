package com.sp.productmanagement.command.rest;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sp.productmanagement.command.CreateProductCommand;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductsCommandController {
	private final CommandGateway commandGateway;

	public ProductsCommandController(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}

	@PostMapping
	public String createProduct(@Valid @RequestBody CreateProductRestModel createProductRestModel) {

		CreateProductCommand createProductCommand = CreateProductCommand.builder()
				.title(createProductRestModel.getTitle()).price(createProductRestModel.getPrice())
				.quantity(createProductRestModel.getQuantity()).productId(UUID.randomUUID().toString()).build();

		String returnVal;
		
		returnVal = commandGateway.sendAndWait(createProductCommand);

		return returnVal;
	}
}
