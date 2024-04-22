package com.sp.usersservice;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sp.usersservice.core.data.AuthorityEntity;
import com.sp.usersservice.core.data.AuthorityRepository;
import com.sp.usersservice.core.data.PaymentDetailEntity;
import com.sp.usersservice.core.data.PaymentDetailRepository;
import com.sp.usersservice.core.data.RoleEntity;
import com.sp.usersservice.core.data.RolesRepository;
import com.sp.usersservice.core.data.UserDetailEntity;
import com.sp.usersservice.core.data.UserDetailsRepository;

import jakarta.transaction.Transactional;

@Component
public class InitialUsersSetup {

	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	RolesRepository rolesRepository;
	
	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Autowired
	PaymentDetailRepository paymentDetailRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private final Logger logger = LoggerFactory.getLogger(InitialUsersSetup.class);
	
	@Transactional
	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		logger.info("user service is ready");
		
		AuthorityEntity readAuthority = createAuthority("READ");
		AuthorityEntity writeAuthority = createAuthority("WRITE");
		AuthorityEntity deleteAuthority = createAuthority("DELETE");
		
		createRole(Roles.ROLE_USER.name(),Arrays.asList(readAuthority,writeAuthority));
		
		RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(),Arrays.asList(readAuthority,writeAuthority,deleteAuthority));
		
		if(roleAdmin == null) return;
		
		UserDetailEntity userDetailEntity = userDetailsRepository.findByUsername("admin");
		
		if(userDetailEntity == null) {
			userDetailEntity = new UserDetailEntity();
			
			userDetailEntity.setId(UUID.randomUUID().toString());
			userDetailEntity.setUsername("admin");
			userDetailEntity.setPassword(bCryptPasswordEncoder.encode("admin1234"));
			userDetailEntity.setEmail("demoadmin@gmail.com");
			userDetailEntity.setFirstName("adminFirstName");
			userDetailEntity.setLastName("adminLastName");
			userDetailEntity.setRoles(Arrays.asList(roleAdmin));
			
			userDetailsRepository.save(userDetailEntity);
			
			PaymentDetailEntity paymentDetailEntity = new PaymentDetailEntity();
			paymentDetailEntity.setPaymentId(UUID.randomUUID().toString());
			paymentDetailEntity.setCardNumber("1234");
			paymentDetailEntity.setUserDetailEntity(userDetailEntity);
			
			paymentDetailRepository.save(paymentDetailEntity);
		}
		
		
		
		
		
	}
	
	@Transactional
	private AuthorityEntity createAuthority(String name) {
		AuthorityEntity authorityEntity = authorityRepository.findByName(name);
		
		if(authorityEntity == null) {
			authorityEntity = new AuthorityEntity();		
			authorityEntity.setName(name);
			authorityRepository.save(authorityEntity);
		}
		return authorityEntity;
	}
	
	@Transactional
	private RoleEntity createRole(String name,List<AuthorityEntity> authorities) {
		RoleEntity roleEntity = rolesRepository.findByName(name);
		
		if(roleEntity == null) {
			roleEntity = new RoleEntity();
			roleEntity.setName(name);
			roleEntity.setAuthorities(authorities);
			rolesRepository.save(roleEntity);
		}
		
		return roleEntity;
	}
}
