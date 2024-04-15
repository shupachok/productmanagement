package com.sp.core.events;

import java.util.List;

import com.sp.core.model.ProductOrdered;
import com.sp.core.model.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDispatchedEvent {
	private final String emailId;
	private final String orderId;
	private final User user;
	
	private final List<ProductOrdered> productOrdereds;

}
