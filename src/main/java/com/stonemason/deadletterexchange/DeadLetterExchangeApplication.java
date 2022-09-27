package com.stonemason.deadletterexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.stonemason.deadletterexchange")
public class DeadLetterExchangeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeadLetterExchangeApplication.class, args);
	}

}
