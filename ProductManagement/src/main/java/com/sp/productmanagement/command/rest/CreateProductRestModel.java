package com.sp.productmanagement.command.rest;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class CreateProductRestModel {

	@NotBlank(message = "Product title is a required field")
	private String title;

	private MultipartFile image;
	
	@Min(value=1,message = "Price cannot be lower than 1")
	private BigDecimal price;
	
	@Min(value=1,message = "Quantity cannot be lower than 1")
	@Max(value=100,message = "Quantity cannot be larger than 100")
	private Integer quantity;
}