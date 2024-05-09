package com.sp.productmanagement.command.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sp.productmanagement.command.CreateProductCommand;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductsCommandController {
	private final CommandGateway commandGateway;
	
	private final Environment env;

	
	public ProductsCommandController(CommandGateway commandGateway, Environment env) {
		this.commandGateway = commandGateway;
		this.env = env;
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
	public String createProduct(@Valid @ModelAttribute CreateProductRestModel createProductRestModel) throws IOException {

		String pathImage = uploadImage(createProductRestModel.getImage());
		
		CreateProductCommand createProductCommand = CreateProductCommand.builder()
				.title(createProductRestModel.getTitle()).price(createProductRestModel.getPrice())
				.pathImage(pathImage)
				.quantity(createProductRestModel.getQuantity()).productId(UUID.randomUUID().toString()).build();

		String returnVal;
		
		returnVal = commandGateway.sendAndWait(createProductCommand);

		return returnVal;
	}
	
    public String uploadImage(MultipartFile image) throws IOException {

        if (image.isEmpty()) {
            throw new IOException("empty file");
        }

        String fileName = image.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);

        if (!Arrays.asList("jpg", "jpeg", "png", "gif").contains(extension.toLowerCase())) {
        	throw new IOException("Invalid image format. Supported formats: jpg, jpeg, png, gif");
        }

        String newFileName = UUID.randomUUID().toString() + "." + extension;
        
        String newpathFile = env.getProperty("upload.image.path") + "/" + newFileName;
        
        Path path = Paths.get(newpathFile);
        Files.write(path, image.getBytes());
        
        return newpathFile;

    }

}
