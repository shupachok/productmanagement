package com.sp.ordersservice.query;

import lombok.Value;

@Value
public class FindOrderSummaryQuery {

	private final String orderId;
}
