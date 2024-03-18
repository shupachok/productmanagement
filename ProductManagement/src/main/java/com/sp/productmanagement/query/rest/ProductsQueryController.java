package com.sp.productmanagement.query.rest;

import java.util.List;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sp.productmanagement.query.FindProductsQuery;

@RestController
@RequestMapping("/products")
public class ProductsQueryController {

	@Autowired
	QueryGateway queryGateway;

	@GetMapping
	public List<ProductsRestModel> getProduct() {
		
		FindProductsQuery findProductsQuery = new FindProductsQuery();
		List<ProductsRestModel> products = queryGateway.query(findProductsQuery
				,ResponseTypes.multipleInstancesOf(ProductsRestModel.class)).join();
		
		return products;
	}
}
