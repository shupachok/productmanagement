package com.sp.paymentsservice.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class PaymentEntity {
	@Id
	private String paymentId;
	@Column
	public String orderId;
}
