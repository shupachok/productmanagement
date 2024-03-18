package com.sp.productmanagement.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsLookupRepository extends JpaRepository<ProductLookupEntity, String>{
	ProductLookupEntity findByProductIdOrTitle(String productId,String title);
}
