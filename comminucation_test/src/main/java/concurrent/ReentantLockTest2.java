package concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentantLockTest2 {

	static List<Integer> list = new ArrayList<>();

	public static void change() {
		list.add(1);
	}

	public static void main(String[] args) {

		ExecutorService ec = Executors.newFixedThreadPool(10);

		final ReentrantLock lock = new ReentrantLock();
		for (int i = 0; i < 100; i++) {
			ec.submit(new Runnable() {
				@Override
				public void run() {

					try {
						lock.lock();
						 
						ReentantLockTest2.change();
					} finally {
						lock.unlock();
					}
				}
			});
		}
		//不在接收提交 然后等全部做完就关闭
		ec.shutdown();

		try {
			ec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		System.out.println(list.size());

	}

}
