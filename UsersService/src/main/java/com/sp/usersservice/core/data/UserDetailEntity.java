package com.sp.usersservice.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "userdetails")
@Data
public class UserDetailEntity {
	@Id
	@Column(unique = true)
	private String userId;
	
	private String username;
	private String password;
	private String email;
	
	private String firstName;
	private String lastName;
	
	@OneToOne(mappedBy = "userDetailEntity")
	private PaymentDetailEntity paymentDetailEntity;
	
	@ManyToOne
	private RoleEntity role;


}
