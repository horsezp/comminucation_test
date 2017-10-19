package concurrent;

//Runnable�Ĵ�����Ա�����̹߳���Threadʵ������
//�ʺ��ڶ������̴߳���ͳһ��Դ�������
public class ThreadAndRunnable {

	public static void main(String[] args) {
		MyThread mt = new MyThread();
		// ͬ������3���߳�
		Thread t1 = new Thread(mt, "�߳�һ");
		Thread t2 = new Thread(mt, "�̶߳�");
		Thread t3 = new Thread(mt, "�߳���");
		// �����߳�
		t1.start();
		t2.start();
		t3.start();
	}

}

class MyThread implements Runnable {
	private int num = 5;// ģ�⻹ʣ��5��Ʊ

	@Override
	public void run() {
		while (num > 0) {
			num--;
			System.out.println(Thread.currentThread().getName() + "������һ��Ʊ��ʣ��Ʊ��Ϊ" + num);
		}
	}
}
