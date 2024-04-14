package com.sp.ordersservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderedRepository extends JpaRepository<ProductOrderedEntity, String> {

}
