package com.didispace.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.didispace.platform.annotation.TxTransaction;
import com.didispace.platform.transaction.TxTransactionContext;
import com.didispace.platform.util.Constants;

@RestController
public class ComputeController {

	private final Logger logger = Logger.getLogger(getClass());

	@Autowired
	private DiscoveryClient client;

	@TxTransaction(value = Constants.Type.ACTION, confirmMethod = "addConfirm", cancelMethod = "addCancel")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Integer add(@RequestBody TxTransactionContext context, @RequestParam Integer a, @RequestParam Integer b) {
		System.out.println("服务提供端被访问到");
		ServiceInstance instance = client.getLocalServiceInstance();
		Integer r = a + b;
		logger.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
		return r;
	}
	
	@RequestMapping(value = "/addConfirm", method = RequestMethod.POST)
	public Integer addConfirm(@RequestParam Integer a, @RequestParam Integer b) {
		System.out.println("服务端确认操作被执行了");
		ServiceInstance instance = client.getLocalServiceInstance();
		Integer r = a + b;
		logger.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
		return r;
	}
	
	@RequestMapping(value = "/addCancel", method = RequestMethod.POST)
	public Integer addCancel(@RequestParam Integer a, @RequestParam Integer b) {
		System.out.println("服务取消操作被执行了");
		ServiceInstance instance = client.getLocalServiceInstance();
		Integer r = a + b;
		logger.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
		return r;
	}
}
