package com.sp.ordersservice.query;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.sp.core.model.OrderDetail;
import com.sp.core.model.OrderSummary;
import com.sp.core.model.ProductOrdered;
import com.sp.core.query.FetchOrderDetailsQuery;
import com.sp.ordersservice.core.data.OrderEntity;
import com.sp.ordersservice.core.data.OrdersRepository;
import com.sp.ordersservice.core.data.ProductOrderedEntity;

@Component
public class OrderQuriesHandler {

	private final OrdersRepository ordersRepository;

	public OrderQuriesHandler(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
	}
	
	@QueryHandler
	public OrderSummary findOrderSummary(FindOrderSummaryQuery findOrderSummaryQuery) {
		OrderEntity orderEntity = ordersRepository.findByOrderId(findOrderSummaryQuery.getOrderId());
		return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(),"");
	}
	
	@QueryHandler
	public OrderDetail findOrderDetail(FetchOrderDetailsQuery fetchOrderDetailsQuery) {
		OrderEntity orderEntity = ordersRepository.findByOrderId(fetchOrderDetailsQuery.getOrderId());
		
		List<ProductOrderedEntity> productOrderedEntities = orderEntity.getProductOrdereds();
		
		OrderDetail orderDetail = OrderDetail.builder()
				.orderId(orderEntity.getOrderId())
				.userId(orderEntity.getUserId())
				.addressId(orderEntity.getAddressId())
				.build();
				
		List<ProductOrdered> productOrdereds = new ArrayList<>();
		
		for(ProductOrderedEntity productOrderedEntity: productOrderedEntities) {
			ProductOrdered productOrdered = ProductOrdered.builder()
					.productId(productOrderedEntity.getProductId())
					.title(productOrderedEntity.getTitle())
					.price(productOrderedEntity.getPrice())
					.build();
		}
		
		orderDetail.setProductOrdereds(productOrdereds);
			
		return orderDetail;
	}
	
	
}
