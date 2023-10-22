package com.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.bookstore")
public class BookStoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookStoreApiApplication.class, args);
	}

}
