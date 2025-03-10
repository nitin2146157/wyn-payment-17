package com.wyn.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class WynPayment17Application {

	public static void main(String[] args) {
		SpringApplication.run(WynPayment17Application.class, args);
	}

}
