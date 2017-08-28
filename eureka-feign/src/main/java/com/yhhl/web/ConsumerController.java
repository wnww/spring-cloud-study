package com.yhhl.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yhhl.service.ComputeClient;

@RestController
public class ConsumerController {

	
	@Autowired
	private ComputeClient computeClient;
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public Integer add() {
        return computeClient.add(10, 20);
    }
}
