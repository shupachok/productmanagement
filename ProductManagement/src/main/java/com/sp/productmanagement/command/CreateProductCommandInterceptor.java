package com.sp.productmanagement.command;

import java.util.List;
import java.util.function.BiFunction;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
//import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sp.productmanagement.core.data.ProductLookupEntity;
import com.sp.productmanagement.core.data.ProductsLookupRepository;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

	private final ProductsLookupRepository productsLookupRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

	public CreateProductCommandInterceptor(ProductsLookupRepository productsLookupRepository) {
		this.productsLookupRepository = productsLookupRepository;
	}

	@Override
	public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
			List<? extends CommandMessage<?>> messages) {

		return (index, command) -> {
			LOGGER.info("Intercepted command:" + command.getPayloadType());

			if (CreateProductCommand.class.equals(command.getPayloadType())) {

				CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

				ProductLookupEntity productEntity = productsLookupRepository
						.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
				if (productEntity != null) {
					throw new IllegalStateException(
							String.format("Product with product id %s or title %s already exist",
									createProductCommand.getProductId(), createProductCommand.getTitle()));
				}
			}
			return command;
		};
	}

}
