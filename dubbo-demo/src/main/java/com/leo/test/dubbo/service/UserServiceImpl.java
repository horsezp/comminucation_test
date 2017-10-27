package com.leo.test.dubbo.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version = "1.0")
public class UserServiceImpl implements UserService {

	@Value("${spring.dubbo.protocol.port}")
	private String portNumber;

	@Override
	public String said(String name) {


		System.out.println(portNumber + "" + name);
		
		try {
			return "response from " + InetAddress.getLocalHost().getHostAddress()  + " " +portNumber + name;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

}
