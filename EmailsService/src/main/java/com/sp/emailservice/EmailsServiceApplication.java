package com.sp.emailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.sp.core.config.AxonConfig;

@SpringBootApplication
@Import({ AxonConfig.class })
public class EmailsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailsServiceApplication.class, args);
	}

}
