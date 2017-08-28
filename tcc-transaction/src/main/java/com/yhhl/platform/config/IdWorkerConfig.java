package com.yhhl.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yhhl.platform.util.IdWorker;

@Configuration
@ConfigurationProperties(prefix = "idWorker")
public class IdWorkerConfig {

	private long workerId;
	private long dataCenterId;

	@Bean
	IdWorker idWorker() {
		return new IdWorker(workerId, dataCenterId);
	}
}
