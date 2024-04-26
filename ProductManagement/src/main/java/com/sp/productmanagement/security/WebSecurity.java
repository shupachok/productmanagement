package com.sp.productmanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class WebSecurity {

	private Environment environment;

	public WebSecurity(Environment environment) {
		this.environment = environment;
	}

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		AuthenticationManagerBuilder authenticationManagerBuilder = 
				http.getSharedObject(AuthenticationManagerBuilder.class);
		
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

		http
		.authorizeHttpRequests(
				auth -> auth.anyRequest().permitAll()
		)
        .addFilter(new AuthorizationFilter(authenticationManager, environment))
        .authenticationManager(authenticationManager)
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(conf -> conf.disable())
		.headers(conf -> conf.frameOptions(foption -> foption.disable()));
		
		
		return http.build();

	}

}
