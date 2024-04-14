package com.sp.core.query;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FetchOrderDetailsQuery {
	private String orderId;
}
