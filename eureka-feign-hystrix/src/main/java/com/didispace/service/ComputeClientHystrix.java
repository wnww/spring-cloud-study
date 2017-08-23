package com.didispace.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.didispace.platform.transaction.TxTransactionContext;

@Service
public class ComputeClientHystrix implements ComputeClient{

	@Override
	public Integer add(@RequestBody TxTransactionContext context) {
		return -9999;
	}
}
