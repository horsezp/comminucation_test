package concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
//可以检测一个线程的运行情况 是否结束 还可以取消 同时可以获取返回值
public class TestFutureTask {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final ExecutorService exec = Executors.newFixedThreadPool(5);
		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				Thread.sleep(1000 * 5);
				return "Other less important but longtime things.";
			}
		};
		Future<String> task = exec.submit(call);
		// 重要的事情
		Thread.sleep(1000 * 3);
		System.out.println("Let’s do important things.");
		// 其他不重要的事情
		// Future的重要方法包括get()和cancel()，get()获取数据对象，如果数据没有加载，就会阻塞直到取到数据，而
		// cancel()是取消数据加载。
		String obj = task.get();
		System.out.println(obj);
		// 关闭线程池
		exec.shutdown();
	}
}