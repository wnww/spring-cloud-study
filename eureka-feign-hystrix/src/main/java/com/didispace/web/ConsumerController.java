package com.didispace.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.didispace.platform.annotation.TxTransaction;
import com.didispace.platform.transaction.ResultEntity;
import com.didispace.platform.transaction.TxTransactionContext;
import com.didispace.platform.util.Constants;
import com.didispace.service.ComputeClient;

@RestController
public class ConsumerController {

	
	@Autowired
	private ComputeClient computeClient;
	
	@TxTransaction(value=Constants.Type.ACTIVITY, confirmMethod="addConfirm", cancelMethod="addCancel")
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public ResultEntity add() {
		System.out.println("================业务发起端被调用了");
		TxTransactionContext context = new TxTransactionContext();
		context.setObj(new Object[]{10,20});
		ResultEntity re = computeClient.add(context);
		System.out.println("远程返回的结果=========："+re.getFlag()+" "+re.getResultMap().get("resultValue"));
		context.setObj(new Object[]{20,60});
		re = computeClient.add(context);
		System.out.println("远程返回的结果=========："+re.getFlag()+" "+re.getResultMap().get("resultValue"));
		//throw new RuntimeException("aaa");
        return re;
    }
	
	@RequestMapping(value = "/addConfirm", method = RequestMethod.POST)
	public ResultEntity addConfirm() {
		System.out.println("activity==================addConfirm被调用了");
		ResultEntity re = new ResultEntity();
		re.setFlag(Constants.ResultStatus.TRUE);
		return re;
    }
	
	@RequestMapping(value = "/addCancel", method = RequestMethod.POST)
	public ResultEntity addCancel() {
		System.out.println("activity================addCancel被调用了");
		ResultEntity re = new ResultEntity();
		re.setFlag(Constants.ResultStatus.TRUE);
		return re;
    }
}
