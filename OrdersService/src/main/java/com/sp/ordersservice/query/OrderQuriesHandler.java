package com.sp.ordersservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.sp.core.model.OrderSummary;
import com.sp.ordersservice.core.data.OrderEntity;
import com.sp.ordersservice.core.data.OrdersRepository;

@Component
public class OrderQuriesHandler {

	private final OrdersRepository ordersRepository;

	public OrderQuriesHandler(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
	}
	
	@QueryHandler
	public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
		OrderEntity orderEntity = ordersRepository.findByOrderId(findOrderQuery.getOrderId());
		return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(),"");
	}
	
	
}
