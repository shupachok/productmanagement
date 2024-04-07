package com.sp.usersservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {
	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests(auth -> auth
				  .anyRequest().permitAll());
		
		http.csrf( conf -> conf.disable());
		
		
		http.headers( conf -> conf.frameOptions(foption -> foption.disable()));
		return http.build();

	}

}
