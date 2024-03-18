package com.sp.productmanagement.core.errorhandling;

import java.util.Date;

import org.axonframework.commandhandling.CommandExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ProductServiceErrorHandler {

	@ExceptionHandler(value = {IllegalStateException.class})
	public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex,
			WebRequest rq){
		ErrorMessage message = new ErrorMessage(new Date(), ex.getMessage());
		
		return new ResponseEntity<>(message,
				new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR);	
	}

	
	@ExceptionHandler(value = {CommandExecutionException.class})
	public ResponseEntity<Object> handleCommandExectionException(CommandExecutionException ex,
			WebRequest rq){
		ErrorMessage message = new ErrorMessage(new Date(), ex.getMessage());
		
		return new ResponseEntity<>(message,
				new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<Object> handleOtherException(Exception ex,
			WebRequest rq){
		ErrorMessage message = new ErrorMessage(new Date(), ex.getMessage());
		
		return new ResponseEntity<>(message,
				new HttpHeaders(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
