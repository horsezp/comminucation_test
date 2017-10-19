package concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ReentantLockTest {

	static int count = 0;
	
	public static void change() {
		count = count+1;
	}

	public static void main(String[] args) {

		ExecutorService ec = Executors.newFixedThreadPool(100);
		final CountDownLatch begin = new CountDownLatch(10000);

		final ReentrantLock lock = new ReentrantLock();
		for (int i = 0; i < 10000; i++) {
			begin.countDown();
			ec.submit(new Runnable() {
				@Override
				public void run() {
					try {
						begin.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//					try {
//						lock.lock();
//						ReentantLockTest.change();
//					} finally {
//						lock.unlock();
//					}
					synchronized(this){
						ReentantLockTest.change();
					}
				}
			});
		}

		ec.shutdown();

		System.out.println(count);
		//这里的值打出来不是10000 原因是每个线程都有自己的高速缓存 count 的值已经被放到
		//线程空间里面去了 

	}

}
