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
	public Integer add(@RequestBody TxTransactionContext tx) {
		System.out.println("\r\n服务提供端被访问到");
		ServiceInstance instance = client.getLocalServiceInstance();
		Object[] obj = tx.getObj();
		Integer r = Integer.parseInt(obj[0].toString()) + Integer.parseInt(obj[1].toString());
		logger.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
		return r;
	}
	
	@RequestMapping(value = "/addConfirm", method = {RequestMethod.POST,RequestMethod.GET})
	public Integer addConfirm(@RequestBody TxTransactionContext tx) {
		System.out.println("_+_+_+_+_+_+_+_+_+_+_");
		Object[] obj = tx.getObj();
		int r = Integer.parseInt(obj[1].toString())-Integer.parseInt(obj[0].toString());
		System.out.println("\r\n服务端addConfirm被调用了，结果："+r);
		return r;
	}
	
	@RequestMapping(value = "/addCancel", method = {RequestMethod.POST,RequestMethod.GET})
	public Integer addCancel(@RequestBody TxTransactionContext tx) {
		Object[] obj = tx.getObj();
		int r = (Integer.parseInt(obj[0].toString())-Integer.parseInt(obj[1].toString()))/2;
		System.out.println("\r\n服务端addCancel被调用了，结果："+r);
		return r;
	}
}
