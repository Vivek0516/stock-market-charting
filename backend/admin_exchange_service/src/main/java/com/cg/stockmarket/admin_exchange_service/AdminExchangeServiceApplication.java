package com.cg.stockmarket.admin_exchange_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cg.stockmarket.admin_exchange_service.client")
public class AdminExchangeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminExchangeServiceApplication.class, args);
	}

}
