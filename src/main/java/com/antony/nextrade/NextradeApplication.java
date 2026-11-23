package com.antony.nextrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class NextradeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NextradeApplication.class, args);
	}

}
