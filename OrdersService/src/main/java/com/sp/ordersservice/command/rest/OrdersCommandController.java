package com.sp.ordersservice.command.rest;

import java.util.ArrayList;
import java.util.List;
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

import com.sp.core.model.OrderStatus;
import com.sp.core.model.OrderSummary;
import com.sp.core.model.ProductOrdered;
import com.sp.ordersservice.command.commands.CreateOrderCommand;
import com.sp.ordersservice.query.FindOrderSummaryQuery;

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

		List<ProductOrdered> productOrdereds = new ArrayList<>();

		List<CreateProductOrderedRestModel> createProductOrdereds = order.getProductOrdereds();
		for (CreateProductOrderedRestModel createProductOrdered : createProductOrdereds) {

			String productOrderedId = UUID.randomUUID().toString();
			ProductOrdered productOrdered = ProductOrdered.builder()
					.productOrderedId(productOrderedId)
					.productId(createProductOrdered.getProductId())
					.title(createProductOrdered.getTitle())
					.price(createProductOrdered.getPrice())
					.quantity(createProductOrdered.getQuantity())
					.build();
			productOrdereds.add(productOrdered);
		}

		CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
				.orderId(orderId).userId(order.getUserId())
				.addressId(order.getAddressId())
				.productOrdered(productOrdereds)
				.build();

		SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult = queryGateway.subscriptionQuery(
				new FindOrderSummaryQuery(orderId), ResponseTypes.instanceOf(OrderSummary.class),
				ResponseTypes.instanceOf(OrderSummary.class));

		try {
			commandGateway.sendAndWait(createOrderCommand);
			return queryResult.updates().blockFirst();
		} finally {
			queryResult.close();
		}
	}

}
