package com.wyn.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.wyn.payment.util.SecretManagerUtil;

@SpringBootApplication
@EnableTransactionManagement
public class WynPayment17Application {

	public static void main(String[] args) {
		// Fetch the password **before** starting Spring Boot
		String password = SecretManagerUtil.getSecret("secret-manager-448804", "PAYMENT_SYSTEM_DEV_DB", "2");

		System.out.println("Password from google Secret Manger fetched: " + password);
		// Set it as a system property so Spring picks it up
		System.setProperty("spring.datasource.password", password);

		System.out.println("Database password fetched and set before initialization.");

		// Now start Spring Boot
		SpringApplication.run(WynPayment17Application.class, args);
	}

}
