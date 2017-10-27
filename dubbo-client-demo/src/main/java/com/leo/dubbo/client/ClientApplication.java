package com.leo.dubbo.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) throws InterruptedException {

		ConfigurableApplicationContext applicationContext = SpringApplication.run(ClientApplication.class, args);

		AtomicInteger count0 = new AtomicInteger(0);
		AtomicInteger count1 = new AtomicInteger(0);
		ExecutorService executor = Executors.newFixedThreadPool(100);
		// for (int i = 0; i < 1000; i++) {
		// executor.submit(new Runnable() {
		//
		// @Override
		// public void run() {
		// ClientService clientService = (ClientService)
		// applicationContext.getBean("clientService");
		// int v = clientService.doSomething();
		// if (v == 20880) {
		// count0.getAndIncrement();
		// } else {
		// count1.getAndIncrement();
		// }
		//
		// }
		// });
		// }
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		// 在随机下的情况下 基本接近 50 50
		// 在轮询的情况下就是50 50
		// 使用最好并发的情况下 接近 50 50
		System.out.println(count0);
		System.out.println(count1);
		
		ExecutorService executor2 = Executors.newFixedThreadPool(10);
		ClientService clientService = (ClientService) applicationContext.getBean("clientService");
		
		for (int i = 0; i < 100; i++) {
			executor2.submit(new Runnable() {
				@Override
				public void run() {
					try {
						clientService.tryFailOver();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}});
			
		}
		//在高并发的情况下 如果请求已经超过了服务那边的线程池 会被马上拒绝 然后得到以下的信息
		// testAndFail_0 could not be queued for execution and no fallback available.
		
		executor2.shutdown();
		executor2.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

		applicationContext.close();
	}

}
