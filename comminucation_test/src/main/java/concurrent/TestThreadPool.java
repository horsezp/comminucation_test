package concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class TestThreadPool {
	static int i = 0;

	public static void main(String args[]) throws InterruptedException {
		// only two threads
		ExecutorService exec = Executors.newFixedThreadPool(2);

		final ReentrantLock lock = new ReentrantLock();

		for (int index = 0; index < 100; index++) {
			Runnable run = new Runnable() {
				public void run() {
					try {
						lock.lock();
						TestThreadPool.i = i + 1;
					} finally {
						lock.unlock();
					}
				}
			};
			exec.execute(run);
		}
		// must shutdown
		exec.shutdown();
		
		System.out.println(i);
	}
}