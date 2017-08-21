package com.didispace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class ComputeService {

	@Autowired
	RestTemplate restTemplate;
	
	@HystrixCommand(fallbackMethod="addServiceFallback")
	public String addService() {
        return restTemplate.getForEntity("http://COMPUTE-SERVICE/add?a=10&b=20", String.class).getBody();
    }
	
	// 这个方法对应着 @HystrixCommand里的fallbackMethod
	public String addServiceFallback() {
        return "error";
    }
}