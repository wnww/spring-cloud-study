package com.didispace.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.didispace.platform.transaction.ResultEntity;
import com.didispace.platform.transaction.TxTransactionContext;

@FeignClient(value = "compute-service", fallback = ComputeClientHystrix.class) // 注册的服务名，就是服务提供者的ServiceId
public interface ComputeClient {

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	ResultEntity add(@RequestBody TxTransactionContext context);
}
