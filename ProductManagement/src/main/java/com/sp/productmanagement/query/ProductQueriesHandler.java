package com.sp.productmanagement.query;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sp.productmanagement.core.data.ProductEntity;
import com.sp.productmanagement.core.data.ProductsRepository;
import com.sp.productmanagement.query.rest.ProductsRestModel;

@Component
public class ProductQueriesHandler {
	private final ProductsRepository productsRepository;

	public ProductQueriesHandler(ProductsRepository productsRepository) {
		super();
		this.productsRepository = productsRepository;
	}

	@QueryHandler
	public List<ProductsRestModel> findProducts(FindProductsQuery findProductsQuery){
		List<ProductsRestModel> products = new ArrayList<>();
		List<ProductEntity> productsEntityList = productsRepository.findAll();
		
		for(ProductEntity productEntity:productsEntityList) {
			ProductsRestModel productRestModel = new ProductsRestModel();
			BeanUtils.copyProperties(productEntity, productRestModel);
			products.add(productRestModel);
		}
		return products;
	}
}
