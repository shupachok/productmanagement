package com.sp.productmanagement.command;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;

import com.sp.productmanagement.core.data.ProductLookupEntity;
import com.sp.productmanagement.core.data.ProductsLookupRepository;
import com.sp.productmanagement.core.event.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {

	public final ProductsLookupRepository productsLookupRepository;

	public ProductLookupEventsHandler(ProductsLookupRepository productsLookupRepository) {
		this.productsLookupRepository = productsLookupRepository;
	}

	@EventHandler
	public void on(ProductCreatedEvent event) {

		ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.getProductId(), event.getTitle());
		productsLookupRepository.save(productLookupEntity);
	}
	
	@ResetHandler
	public void reset() {
		productsLookupRepository.deleteAll();
	}
	
}
