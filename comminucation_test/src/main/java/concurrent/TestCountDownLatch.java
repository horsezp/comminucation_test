package concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*CountDownLatch最重要的方法是countDown()和await()，
 * 前者主要是倒数一次，后者是等待倒数到0，如果没有到达0，就只有阻塞等待了。 

一个CountDouwnLatch实例是不能重复使用的，也就是说它是一次性的，
锁一经被打开就不能再关闭使用了，如果想重复使用，请考虑使用CyclicBarrier。 
*/
public class TestCountDownLatch {
	public static void main(String[] args) throws InterruptedException {
		// 开始的倒数锁
		final CountDownLatch begin = new CountDownLatch(1);
		// 结束的倒数锁
		final CountDownLatch end = new CountDownLatch(10);
		// 十名选手
		final ExecutorService exec = Executors.newFixedThreadPool(10);
		for (int index = 0; index < 10; index++) {
			final int NO = index + 1;
			Runnable run = new Runnable() {
				public void run() {
					try {
						begin.await();
						Thread.sleep((long) (Math.random() * 10000));
						System.out.println("No." + NO + " arrived");
					} catch (InterruptedException e) {
					} finally {
						end.countDown();
					}
				}
			};
			exec.submit(run);
		}
		System.out.println("Game Start");
		//每个线程掉用一次就减一
		begin.countDown();
		//等待到 倒数为0
		end.await();
		System.out.println("Game Over");
		exec.shutdown();
	}
}