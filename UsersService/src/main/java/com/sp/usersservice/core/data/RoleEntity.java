package com.sp.usersservice.core.data;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class RoleEntity {
	@Id
	private String id;

	@Column(nullable = false, length = 50, unique = true)
	private String name;
	
	@OneToMany(mappedBy = "role")
	List<UserDetailEntity> userDetailEntity = new ArrayList<>();
	
}
