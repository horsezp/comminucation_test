package com.leo.test.dubbo.service;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version = "1.0", filter = "hystrix", timeout =200)
public class HystrixCommandServiceImpl implements HystrixCommandService {

	@Value("${spring.dubbo.protocol.port}")
	private String portNumber;

	@Override
	public String testAndFail() throws Exception {

		String value = "response from " + InetAddress.getLocalHost().getHostAddress() + " " + portNumber + "Time:"
				+ System.currentTimeMillis();
		Thread.sleep(300);
		return value;
	}

}
