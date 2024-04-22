package com.sp.usersservice.core.data;


import java.util.Collection;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
	private String id;
	
	private String username;
	private String password;
	private String email;
	
	private String firstName;
	private String lastName;
	
	@OneToOne(mappedBy = "userDetailEntity")
	private PaymentDetailEntity paymentDetail;
	
	@ManyToMany(
			cascade = CascadeType.PERSIST,
			fetch = FetchType.EAGER)
	@JoinTable(name="users_roles",joinColumns = @JoinColumn(name="users_id",referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name="roles_id",referencedColumnName = "id"))
	private List<RoleEntity> roles;


}
