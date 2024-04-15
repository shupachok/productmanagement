package com.sp.core.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDetail {
	private String orderId;
	private String userId;
	private String addressId;
	
	private List<ProductOrdered> productOrdereds;
}
