package com.sp.usersservice.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class RoleEntity implements Serializable {
	
	private static final long serialVersionUID = 4500626056014015733L;

	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false, length = 50, unique = true)
	private String name;
	
	@ManyToMany(mappedBy = "roles")
	List<UserDetailEntity> users;
	
	@ManyToMany(
			cascade = CascadeType.PERSIST,
			fetch = FetchType.EAGER)
	@JoinTable(name="roles_authorities",joinColumns = @JoinColumn(name="roles_id",referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name="authorities_id",referencedColumnName = "id"))
	private List<AuthorityEntity> authorities;
	
}
