package concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/*��ϵͳ���ź����Ǹ�����Ҫ�ĸ���ڽ��̿��Ʒ��涼��Ӧ�á�
 * Java�������Semaphore���Ժ���������ź������ƣ�
 * Semaphore���Կ���ĳ����Դ�ɱ�ͬʱ���ʵĸ�����acquire()��ȡһ����ɣ�
 * ���û�о͵ȴ�����release()�ͷ�һ����ɡ�
 * ������Windows�¿������ù����ļ������ͻ��˷��ʸ����� 

  Semaphoreά���˵�ǰ���ʵĸ������ṩͬ�����ƣ�����ͬʱ���ʵĸ�����
   �����ݽṹ��������Ա��桰���ޡ��Ľڵ㣬��Semaphore����ʵ�����޴�С������
   ����������ReentrantLockҲ����ʵ�ָù��ܣ���ʵ����Ҫ����Щ������ҲҪ����Щ�� 
 
   Ŀ����������Դ����ͬʱ���̷߳��ʵ�����

*/

public class TestSemaphore {
	public static void main(String[] args) {
		// �̳߳�
		ExecutorService exec = Executors.newCachedThreadPool();
		// ֻ��5���߳�ͬʱ����
		final Semaphore semp = new Semaphore(5);
		// ģ��20���ͻ��˷���
		for (int index = 0; index < 20; index++) {
			final int NO = index;
			Runnable run = new Runnable() {
				public void run() {
					try {
						// ��ȡ���
						semp.acquire();
						System.out.println("Accessing: " + NO);
						Thread.sleep((long) (Math.random() * 10000));
						// ��������ͷ�
						semp.release();
					} catch (InterruptedException e) {
					}
				}
			};
			exec.execute(run);
		}
		// �˳��̳߳�
		exec.shutdown();
	}
}