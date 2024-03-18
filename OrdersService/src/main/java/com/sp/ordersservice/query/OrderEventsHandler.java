package com.sp.ordersservice.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sp.core.events.OrderApprovedEvent;
import com.sp.ordersservice.core.data.OrderEntity;
import com.sp.ordersservice.core.data.OrdersRepository;
import com.sp.ordersservice.core.events.OrderCreatedEvent;
import com.sp.ordersservice.core.events.OrderRejectedEvent;

@Component
public class OrderEventsHandler {

	private final OrdersRepository ordersRepository;

	public OrderEventsHandler(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
	}

	@EventHandler
	public void on(OrderCreatedEvent orderCreatedEvent) {
		OrderEntity entity = new OrderEntity();
		BeanUtils.copyProperties(orderCreatedEvent, entity);
		ordersRepository.save(entity);
	}

	@EventHandler
	public void on(OrderApprovedEvent orderApprovedEvent) {
		OrderEntity orderEntity = ordersRepository.findByOrderId(orderApprovedEvent.getOrderId());
		if (orderEntity == null) {
			return;
		}

		orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
		ordersRepository.save(orderEntity);

	}
	
	@EventHandler
	public void on(OrderRejectedEvent orderRejectedEvent) {
		OrderEntity orderEntity = ordersRepository.findByOrderId(orderRejectedEvent.getOrderId());
		orderEntity.setOrderStatus(orderRejectedEvent.getOrderStatus());
		ordersRepository.save(orderEntity);
	}
}
