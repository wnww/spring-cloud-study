package com.yhhl.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.yhhl.platform.transaction.ResultEntity;
import com.yhhl.platform.transaction.TxTransactionContext;
import com.yhhl.platform.util.Constants;

@Service
public class ComputeClientHystrix implements ComputeClient{

	@Override
	public ResultEntity add(@RequestBody TxTransactionContext context) {
		ResultEntity re = new ResultEntity();
		re.setFlag(Constants.ResultStatus.FALSE);
		re.setMessage("远程调用失败，可能是网络异常");
		return re;
	}
}
