package com.didispace.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.didispace.platform.annotation.TxTransaction;
import com.didispace.platform.transaction.TxTransactionContext;
import com.didispace.platform.util.Constants;

@FeignClient(value = "compute-service", fallback = ComputeClientHystrix.class) // 注册的服务名，就是服务提供者的ServiceId
public interface ComputeClient {

	@TxTransaction(value = Constants.Type.CALL)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	Integer add(@RequestBody TxTransactionContext context, @RequestParam(value = "a") Integer a,
			@RequestParam(value = "b") Integer b);
}
