package com.sp.usersservice.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sp.usersservice.core.data.AuthorityEntity;
import com.sp.usersservice.core.data.RoleEntity;
import com.sp.usersservice.core.data.UserDetailEntity;
import com.sp.usersservice.core.data.UserDetailsRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	UserDetailsRepository userDetailsRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserDetailEntity userDetailEntity = userDetailsRepository.findByEmail(username);
		
		if(userDetailEntity == null) 
			throw new UsernameNotFoundException(username);

		List<RoleEntity> roles = userDetailEntity.getRoles();
		
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		for(RoleEntity role:roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			
			List<AuthorityEntity> authorityEntities = role.getAuthorities();
			
			for(AuthorityEntity authorityEntity: authorityEntities) {
				authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
			}
			
		}
		
		return new User(userDetailEntity.getEmail(), userDetailEntity.getPassword(), true, true, true, true,
				authorities);

	}

	@Override
	public UserDetailEntity getUserByEmail(String email) {
		UserDetailEntity userDetailEntity = userDetailsRepository.findByEmail(email);
		return userDetailEntity;
	}

}
