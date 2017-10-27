package com.leo.dubbo.client;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.leo.test.dubbo.service.HystrixCommandService;
import com.leo.test.dubbo.service.UserService;

@Service
public class ClientService {
	
	
	private static Logger logger = Logger.getLogger(ClientService.class);

	@Reference(version = "1.0")
	public UserService userService;
	
	@Reference(version = "1.0")
	public HystrixCommandService hystrixCommandService;

	public int doSomething() {

		String value = userService.said("from client");
		if (value.indexOf("20880") >= 0) {
			return 20880;
		} else {
			return 20881;
		}
	}
	
	public void tryFailOver() {
		try {
			System.out.println(hystrixCommandService.testAndFail());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

}
