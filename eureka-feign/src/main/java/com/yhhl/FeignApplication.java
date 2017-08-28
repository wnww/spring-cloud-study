package com.yhhl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients// 开启Feign功能，用了Feign后就有了客户端负载均衡功能
@EnableDiscoveryClient
@SpringBootApplication
public class FeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeignApplication.class, args);
	}

}
