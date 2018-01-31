package com.stockApp.StockPriceCachingApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages= {"com.trade.controllers","com.trade.exception","com.trade.service"})
public class StockPriceCachingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockPriceCachingAppApplication.class, args);
	}
}
