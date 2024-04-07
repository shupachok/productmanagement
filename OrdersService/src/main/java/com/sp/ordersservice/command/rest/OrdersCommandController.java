package com.sp.ordersservice.command.rest;

import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sp.core.model.OrderSummary;
import com.sp.ordersservice.command.commands.CreateOrderCommand;
import com.sp.ordersservice.query.FindOrderQuery;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

	@Autowired
	private CommandGateway commandGateway;
	
	@Autowired
	private QueryGateway queryGateway;

	@PostMapping
	public OrderSummary createOrder(@RequestBody CreateOrderRestModel order) {
		String orderId = UUID.randomUUID().toString();
		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
				.orderId(orderId)
				.userId(order.getUserId())
				.productId(order.getProductId())
				.quantity(order.getQuantity())
				.addressId(order.getAddressId()).build();
		
		SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult = queryGateway.subscriptionQuery(
				new FindOrderQuery(orderId)
				,ResponseTypes.instanceOf(OrderSummary.class)
				, ResponseTypes.instanceOf(OrderSummary.class));
		
		try {
			commandGateway.sendAndWait(createOrderCommand);
			return queryResult.updates().blockFirst();
		} finally {
			queryResult.close();
		}
	}

	@PutMapping
	public String updateOrder() {
		return "updateOrders";
	}

	@DeleteMapping
	public String deleteMapping() {
		return "deleteMapping";
	}

}
