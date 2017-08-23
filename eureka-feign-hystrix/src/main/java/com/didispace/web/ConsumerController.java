package com.didispace.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.didispace.platform.annotation.TxTransaction;
import com.didispace.platform.transaction.TxTransactionContext;
import com.didispace.platform.util.Constants;
import com.didispace.service.ComputeClient;

@RestController
public class ConsumerController {

	
	@Autowired
	private ComputeClient computeClient;
	
	@TxTransaction(value=Constants.Type.ACTIVITY, confirmMethod="addConfirm", cancelMethod="addCancel")
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public Integer add() {
		System.out.println("业务发起端被调用了");
		TxTransactionContext context = new TxTransactionContext();
		context.setObj(new Object[]{10,20});
        return computeClient.add(context);
    }
	
	@RequestMapping(value = "/addConfirm", method = RequestMethod.POST)
	public Integer addConfirm() {
		System.out.println("activity==addConfirm被调用了");
		return 0;
    }
	
	@RequestMapping(value = "/addCancel", method = RequestMethod.POST)
	public Integer addCancel() {
		System.out.println("activity==addCancel被调用了");
		return 0;
    }
}
