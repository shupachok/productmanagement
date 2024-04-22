package com.sp.usersservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<UserDetailEntity,String > {
	UserDetailEntity findByUsername(String username);
}
