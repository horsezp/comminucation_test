package concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*Java的并发库的CompletionService可以满足这种场景要求。
 * 该接口有两个重要方法：submit()和take()。
 * submit用于提交一个runnable或者callable，
 * 一般会提交给一个线程池处理；
 * 而take就是取出已经执行完毕runnable或者callable实例的Future对象，
 * 如果没有满足要求的，就等待了。 CompletionService还有一个对应的方法poll，
 * 该方法与take类似，只是不会等待，如果没有满足要求，就返回null对象。 
*/
public class TestCompletionService {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService exec = Executors.newFixedThreadPool(10);
		CompletionService<String> serv = new ExecutorCompletionService<>(exec);

		for (int index = 0; index < 5; index++) {
			final int NO = index;
			Callable<String> downImg = new Callable<String>() {
				public String call() throws Exception {
					Thread.sleep((long) (Math.random() * 10000));
					return "Downloaded Image " + NO;
				}
			};
			serv.submit(downImg);
		}

		Thread.sleep(1000 * 2);
		System.out.println("Show web content");
		for (int index = 0; index < 5; index++) {
			//take 会阻塞 用 poll()就不会阻塞
			Future<String> task = serv.take();
			//因為上文是那到已经结束的线程 这里的get操作就不会阻塞了
			String img = task.get();
			System.out.println(img);
		}
		System.out.println("End");
		// 关闭线程池
		exec.shutdown();
	}
}