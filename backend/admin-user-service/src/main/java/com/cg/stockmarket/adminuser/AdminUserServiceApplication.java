package com.cg.stockmarket.adminuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
public class AdminUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminUserServiceApplication.class, args);
	}

}
