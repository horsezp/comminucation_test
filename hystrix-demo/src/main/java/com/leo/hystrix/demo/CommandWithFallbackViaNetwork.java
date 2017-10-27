package com.leo.hystrix.demo;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
//fallback�����߼�����Ƕ��
public class CommandWithFallbackViaNetwork extends HystrixCommand<String> {
	private final int id;

	protected CommandWithFallbackViaNetwork(int id) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceX"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("GetValueCommand")));
		this.id = id;
	}

	@Override
	protected String run() {
		// RemoteService.getValue(id);
		System.out.println("come to here run");
		throw new RuntimeException("force failure for example");
	}

	@Override
	protected String getFallback() {
		System.out.println("come to here getFallback ");
		return new FallbackViaNetwork(id).execute();
	}

	private static class FallbackViaNetwork extends HystrixCommand<String> {
		private final int id;

		public FallbackViaNetwork(int id) {
			super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RemoteServiceX"))
					.andCommandKey(HystrixCommandKey.Factory.asKey("GetValueFallbackCommand"))
					// ʹ�ò�ͬ���̳߳������룬��ֹ�ϲ��̳߳�������Ӱ�콵���߼�.
					.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("RemoteServiceXFallback")));
			this.id = id;
		}

		@Override
		protected String run() {
			return MemCacheClient.getValue(id);
		}

		@Override
		protected String getFallback() {
			return null;
		}
	}

	public static void main(String[] args) {
		
		CommandWithFallbackViaNetwork helloWorldCommand = new CommandWithFallbackViaNetwork(123);
		String result = helloWorldCommand.execute();
		System.out.println(result);

	}
}

class MemCacheClient {

	public static String getValue(int id) {
		return String.valueOf(id);
	}
}