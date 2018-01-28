package com.stockApp.StockPriceCachingApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {"com.controllers","com.exception"})
public class StockPriceCachingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockPriceCachingAppApplication.class, args);
	}
}
