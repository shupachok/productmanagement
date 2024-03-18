package com.sp.paymentsservice.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sp.core.events.PaymentProcessedEvent;
import com.sp.paymentsservice.core.data.PaymentEntity;
import com.sp.paymentsservice.core.data.PaymentsRepository;

@Component
public class PaymentEventsHandler {

	private final PaymentsRepository paymentsRepository;

	public PaymentEventsHandler(PaymentsRepository paymentsRepository) {
		this.paymentsRepository = paymentsRepository;
	}

	@EventHandler
	public void on(PaymentProcessedEvent paymentProcessedEvent) {
		PaymentEntity entity = new PaymentEntity();
		BeanUtils.copyProperties(paymentProcessedEvent, entity);
		paymentsRepository.save(entity);
	}

}
