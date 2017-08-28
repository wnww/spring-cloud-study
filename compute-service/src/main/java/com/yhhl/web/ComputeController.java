package com.yhhl.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yhhl.platform.annotation.TxTransaction;
import com.yhhl.platform.transaction.ResultEntity;
import com.yhhl.platform.transaction.TxTransactionContext;
import com.yhhl.platform.util.Constants;

@RestController
public class ComputeController {

	private final Logger logger = Logger.getLogger(getClass());

	@Autowired
	private DiscoveryClient client;

	@TxTransaction(value = Constants.Type.ACTION, confirmMethod = "addConfirm", cancelMethod = "addCancel")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResultEntity add(@RequestBody TxTransactionContext tx) {
		ResultEntity re = new ResultEntity();
		System.out.println("\r\n服务提供端被访问到");
		ServiceInstance instance = client.getLocalServiceInstance();
		Object[] obj = tx.getObj();
		Integer r = Integer.parseInt(obj[0].toString()) + Integer.parseInt(obj[1].toString());
		logger.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
		re.setFlag(Constants.ResultStatus.TRUE);
		re.setMessage("操作成功");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("resultValue", r);
		re.setResultMap(resultMap);
		return re;
	}
	
	@RequestMapping(value = "/addConfirm", method = {RequestMethod.POST,RequestMethod.GET})
	public ResultEntity addConfirm(@RequestBody TxTransactionContext tx) {
		ResultEntity re = new ResultEntity();
		System.out.println("_+_+_+_+_+_+_+_+_+_+_");
		Object[] obj = tx.getObj();
		int r = Integer.parseInt(obj[1].toString())-Integer.parseInt(obj[0].toString());
		System.out.println("\r\n服务端addConfirm被调用了，结果："+r);
		re.setFlag(Constants.ResultStatus.TRUE);
		re.setMessage("操作成功");
		return re;
	}
	
	@RequestMapping(value = "/addCancel", method = {RequestMethod.POST,RequestMethod.GET})
	public ResultEntity addCancel(@RequestBody TxTransactionContext tx) {
		ResultEntity re = new ResultEntity();
		Object[] obj = tx.getObj();
		int r = (Integer.parseInt(obj[0].toString())-Integer.parseInt(obj[1].toString()))/2;
		System.out.println("\r\n服务端addCancel被调用了，结果："+r);
		re.setFlag(Constants.ResultStatus.TRUE);
		re.setMessage("操作成功");
		return re;
	}
}
