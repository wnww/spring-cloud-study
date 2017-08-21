package com.didispace.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.didispace.platform.transaction.TxTransactionContext;

@Service
public class ComputeClientHystrix implements ComputeClient{

	@Override
	public Integer add(@RequestBody TxTransactionContext context,@RequestParam(value = "a") Integer a, @RequestParam(value = "b") Integer b) {
		return -9999;
	}
}
