package com.sp.productmanagement.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sp.core.events.ProductReservationCanceledEvent;
import com.sp.core.events.ProductReservedEvent;
import com.sp.productmanagement.core.data.ProductEntity;
import com.sp.productmanagement.core.data.ProductsRepository;
import com.sp.productmanagement.core.event.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {
	
	private final ProductsRepository productsRepository;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventsHandler.class); 


	public ProductEventsHandler(ProductsRepository productsRepository) {
		this.productsRepository = productsRepository;
	}
	
	@EventHandler
	public void on(ProductCreatedEvent productCreatedEvent) {
		ProductEntity entity = new ProductEntity();
		BeanUtils.copyProperties(productCreatedEvent, entity);
		productsRepository.save(entity);
		

//		if(true)
//			throw new Exception("An error took place in Product Event Handler");
	}
	
	@EventHandler
	public void on(ProductReservedEvent productReservedEvent) {
		ProductEntity productEntity = productsRepository.findByProductId(productReservedEvent.getProductId());
		productEntity.setQuantity(productEntity.getQuantity() - productReservedEvent.getQuantity());
		productsRepository.save(productEntity);
		
		LOGGER.info("ProductReservedEvent is called for productId = "+productReservedEvent.getProductId());
		
		
	}
	
	@EventHandler
	public void on(ProductReservationCanceledEvent productReservationCanceledEvent) {
		ProductEntity productEntity = productsRepository.findByProductId(productReservationCanceledEvent.getProductId());
		productEntity.setQuantity(productEntity.getQuantity() + productReservationCanceledEvent.getQuantity());
		productsRepository.save(productEntity);
	}
	 
	@ResetHandler
	public void reset() {
		productsRepository.deleteAll();
	}
	
	@ExceptionHandler(resultType = IllegalArgumentException.class)
	public void handleIllegalArgumentException(IllegalArgumentException ex) throws IllegalArgumentException {
		throw ex;
	}
	
	@ExceptionHandler(resultType = Exception.class)
	public void handleException(Exception ex) throws Exception {
		throw ex;
	}
}
