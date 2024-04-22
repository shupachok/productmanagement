package com.sp.usersservice.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "authorities")
@Data
public class AuthorityEntity implements Serializable {

	private static final long serialVersionUID = -1820305337863543164L;

	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false, length = 50, unique = true)
	private String name;
	
	@ManyToMany(mappedBy = "authorities")
	List<RoleEntity> roles;
}
