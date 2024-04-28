package com.sp.usersservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

import com.sp.usersservice.security.service.UserService;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class WebSecurity {

	private UserService userService;

	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private Environment environment;

	public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, Environment environment) {
		this.userService = userService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.environment = environment;
	}

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		AuthenticationManagerBuilder authenticationManagerBuilder = 
				http.getSharedObject(AuthenticationManagerBuilder.class);
		
		authenticationManagerBuilder.userDetailsService(userService)
		.passwordEncoder(bCryptPasswordEncoder);
		
		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        AuthenticationFilter authenticationFilter = 
        		new AuthenticationFilter(authenticationManager,userService,environment);
        authenticationFilter.setFilterProcessesUrl(environment.getProperty("login.url.path"));

		http
//		.authorizeHttpRequests(
//				auth -> auth.anyRequest().permitAll()
//		)
//		.authorizeHttpRequests(
//				auth -> auth.requestMatchers("/**").authenticated()
//		)
		.authorizeHttpRequests(
				auth -> auth.requestMatchers("/h2-console/**").permitAll()
		)
		.authorizeHttpRequests(
				auth -> auth.requestMatchers(HttpMethod.POST,"/users").permitAll()
		)
		.authorizeHttpRequests(
				auth -> auth.anyRequest().authenticated()
		)
		.addFilter(authenticationFilter)
        .addFilter(new AuthorizationFilter(authenticationManager, environment))
        .authenticationManager(authenticationManager)
        .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(conf -> conf.disable())
		.headers(conf -> conf.frameOptions(foption -> foption.disable()));
		
		
		return http.build();

	}

}
