package com.sp.ordersservice.query;

import java.util.List;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sp.core.commands.CancelProductReservationCommand;
import com.sp.core.events.OrderApprovedEvent;
import com.sp.core.model.ProductOrdered;
import com.sp.ordersservice.core.data.OrderEntity;
import com.sp.ordersservice.core.data.OrdersRepository;
import com.sp.ordersservice.core.data.ProductOrderedEntity;
import com.sp.ordersservice.core.data.ProductOrderedRepository;
import com.sp.ordersservice.core.events.OrderConfirmedEvent;
import com.sp.ordersservice.core.events.OrderCreatedEvent;
import com.sp.ordersservice.core.events.OrderRejectedEvent;

@Component
public class OrderEventsHandler {

	private final OrdersRepository ordersRepository;

	private final ProductOrderedRepository productOrderedRepository;

	public OrderEventsHandler(OrdersRepository ordersRepository, ProductOrderedRepository productOrderedRepository) {
		super();
		this.ordersRepository = ordersRepository;
		this.productOrderedRepository = productOrderedRepository;
	}

	@EventHandler
	public void on(OrderCreatedEvent orderCreatedEvent) {
		OrderEntity orderEntity = new OrderEntity();
		BeanUtils.copyProperties(orderCreatedEvent, orderEntity);

		ordersRepository.save(orderEntity);
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
	
	@EventHandler
	public void on(OrderConfirmedEvent orderConfirmedEvent) {
		OrderEntity orderEntity = ordersRepository.findByOrderId(orderConfirmedEvent.getOrderId());
		orderEntity.setOrderStatus(orderConfirmedEvent.getOrderStatus());
		ordersRepository.save(orderEntity);
		
		List<ProductOrdered> productOrdereds = orderConfirmedEvent.getProductOrdered();
		for (ProductOrdered productOrdered : productOrdereds) {

			ProductOrderedEntity productOrderedEntity = new ProductOrderedEntity();
			BeanUtils.copyProperties(productOrdered, productOrderedEntity);
			productOrderedEntity.setOrder(orderEntity);
			
			productOrderedRepository.save(productOrderedEntity);
		}
	}
}
