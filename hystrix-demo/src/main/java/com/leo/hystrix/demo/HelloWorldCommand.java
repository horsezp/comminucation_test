package com.leo.hystrix.demo;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

public class HelloWorldCommand extends HystrixCommand<String> {
	private final String name;

	public HelloWorldCommand(String name) {
		// ��������:ָ����������(CommandGroup)
		// super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"))
				/* ����������ʱʱ��,500���� */
				// .andCommandPropertiesDefaults(
				// HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(500)));
				//
	     /* �����ź������뷽ʽ,Ĭ�ϲ����̳߳ظ��� */  
        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE))); 
        
		System.out.println("execute onCompleted");
		this.name = name;
	}

	@Override
	protected String getFallback() {
		return "exeucute Falled";
	}

	@Override
	protected String run() {
		// sleep 1 ��,���ûᳬʱ
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
		// �����߼���װ��run()������
		return "Hello " + name + " thread:" + Thread.currentThread().getName();
	}

	// ����ʵ��
	public static void main(String[] args) throws Exception {
		// ÿ��Command����ֻ�ܵ���һ��,�������ظ�����,
		// �ظ����ö�Ӧ�쳣��Ϣ:This instance can only be executed once. Please instantiate a new
		// instance.
		HelloWorldCommand helloWorldCommand = new HelloWorldCommand("Synchronous-hystrix");
		// ʹ��execute()ͬ�����ô���,Ч����ͬ��:helloWorldCommand.queue().get();
		String result = helloWorldCommand.execute();
		System.out.println("result=" + result);

		helloWorldCommand = new HelloWorldCommand("Asynchronous-hystrix");
		// �첽����,�����ɿ��ƻ�ȡ���ʱ��,
		Future<String> future = helloWorldCommand.queue();
		// get�������ܳ���command����ĳ�ʱʱ��,Ĭ��:1��
		result = future.get(100, TimeUnit.MILLISECONDS);
		System.out.println("result=" + result);
		System.out.println("mainThread=" + Thread.currentThread().getName());

		// ע��۲����¼�����
		Observable<String> fs = new HelloWorldCommand("World").observe();
		// ע�����ص��¼�
		fs.subscribe(new Action1<String>() {
			@Override
			public void call(String result) {
				// ִ�н������,result ΪHelloWorldCommand���صĽ��
				// �û��Խ�������δ���.
				System.out.println("do something " + result);
			}
		});
		// ע������ִ�����������¼�
		fs.subscribe(new Observer<String>() {
			@Override
			public void onCompleted() {
				// onNext/onError���֮�����ص�
				System.out.println("execute onCompleted");
			}

			@Override
			public void onError(Throwable e) {
				// �������쳣ʱ�ص�
				System.out.println("onError " + e.getMessage());
				e.printStackTrace();
			}

			@Override
			public void onNext(String v) {
				// ��ȡ�����ص�
				System.out.println("onNext: " + v);
			}
		});
	}

}