package com.expensemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.expensemanager"})
@SpringBootApplication
public class ExpenseManager2Application {

	public static void main(String[] args) {
		SpringApplication.run(ExpenseManager2Application.class, args);
	}

}