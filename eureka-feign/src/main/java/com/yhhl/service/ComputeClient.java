package com.yhhl.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("compute-service") // 注册的服务名，就是服务提供者的ServiceId
public interface ComputeClient {

	@RequestMapping(value="/add", method = RequestMethod.GET)
	Integer add(@RequestParam(value = "a") Integer a, @RequestParam(value = "b") Integer b);
}
