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
	
	@TxTransaction(Constants.Type.ACTIVITY)
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public Integer add() {
		System.out.println("业务发起端被调用了");
		TxTransactionContext context = new TxTransactionContext();
        return computeClient.add(context,10, 20);
    }
}
