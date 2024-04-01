package com.sp.usersservice.command.rest;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sp.core.model.PaymentDetails;
import com.sp.usersservice.command.commands.CreateUserCommand;
import com.sp.usersservice.constant.SystemConstant;

@RestController
@RequestMapping("/users")
public class UsersCommandController {

	private final CommandGateway commandGateway;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	
	public UsersCommandController(CommandGateway commandGateway, BCryptPasswordEncoder bCryptPasswordEncoder) {
		super();
		this.commandGateway = commandGateway;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}


	@PostMapping
	public String createUser(@RequestBody CreateUserRestModel createUserRestModel) {
		
		CreatePaymentDetailRestModel paymentDetailsRest = createUserRestModel.getPaymentDetails();
		PaymentDetails paymentDetails = PaymentDetails.builder()
		.name(paymentDetailsRest.getName())
		.cardNumber(paymentDetailsRest.getCardNumber())
		.cvv(paymentDetailsRest.getCvv())
		.validUntilMonth(paymentDetailsRest.getValidUntilMonth())
		.validUntilYear(paymentDetailsRest.getValidUntilYear())
		.paymentId(UUID.randomUUID().toString())
		.build();
		
		CreateUserCommand createUserCommand = CreateUserCommand.builder()
		.userId(UUID.randomUUID().toString())
		.username(createUserRestModel.getUsername())
		.password(bCryptPasswordEncoder.encode(createUserRestModel.getPassword()))
		.roleId(SystemConstant.DEFAULT)
		.email(createUserRestModel.getEmail())
		.firstName(createUserRestModel.getFirstName())
		.lastName(createUserRestModel.getLastName())
		.paymentDetails(paymentDetails)
		.build();
		
		String returnVal = commandGateway.sendAndWait(createUserCommand);
		
		return returnVal;
	}
}
