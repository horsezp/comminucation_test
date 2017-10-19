package concurrent;

//Runnable的代码可以被多个线程共享（Thread实例），
//适合于多个多个线程处理统一资源的情况。
public class ThreadAndRunnable {

	public static void main(String[] args) {
		MyThread mt = new MyThread();
		// 同样创建3个线程
		Thread t1 = new Thread(mt, "线程一");
		Thread t2 = new Thread(mt, "线程二");
		Thread t3 = new Thread(mt, "线程三");
		// 启动线程
		t1.start();
		t2.start();
		t3.start();
	}

}

class MyThread implements Runnable {
	private int num = 5;// 模拟还剩余5张票

	@Override
	public void run() {
		while (num > 0) {
			num--;
			System.out.println(Thread.currentThread().getName() + "卖出了一张票，剩余票数为" + num);
		}
	}
}
