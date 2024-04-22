package com.sp.usersservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<RoleEntity, Long> {

	RoleEntity findByName(String name);
}
