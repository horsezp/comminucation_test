package com.leo.dubbo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext applicationContext = SpringApplication.run(ClientApplication.class, args);

		ClientService clientService =(ClientService)applicationContext.getBean("clientService");
		
		clientService.doSomething();
	}

}
