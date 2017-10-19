package com.leo.test.dubbo.service;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version="1.0")
public class UserServiceImpl implements UserService {

	@Override
	public void said(String name) {
		
		System.out.println(name);

	}

}
