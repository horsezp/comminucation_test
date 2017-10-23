package com.leo.dubbo.client;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.leo.test.dubbo.service.UserService;

@Service
public class ClientService {
	
    @Reference(version = "1.0")
    public UserService userService;
    
    
    public void doSomething() {
    	userService.said("from client");
    }

}
