package com.yhhl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableCircuitBreaker // 启动断路器功能（电路熔断）
@EnableDiscoveryClient
@SpringBootApplication
public class RibbonApplication {

	@Bean // 注册一个bean
	@LoadBalanced// 此注解实现客户端负载均衡
	RestTemplate restTemplate(){
		return new RestTemplate();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(RibbonApplication.class, args);
	}

}
