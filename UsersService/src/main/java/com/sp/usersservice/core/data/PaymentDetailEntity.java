package com.sp.usersservice.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "paymentdetail")
@Data
public class PaymentDetailEntity {

	@Id
	private String paymentId;
	private String name;
	private String cardNumber;
	private int validUntilMonth;
	private int validUntilYear;
	
	@OneToOne
	private UserDetailEntity userDetailEntity;
	private String cvv;
}
