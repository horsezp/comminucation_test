package concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*CountDownLatch����Ҫ�ķ�����countDown()��await()��
 * ǰ����Ҫ�ǵ���һ�Σ������ǵȴ�������0�����û�е���0����ֻ�������ȴ��ˡ� 

һ��CountDouwnLatchʵ���ǲ����ظ�ʹ�õģ�Ҳ����˵����һ���Եģ�
��һ�����򿪾Ͳ����ٹر�ʹ���ˣ�������ظ�ʹ�ã��뿼��ʹ��CyclicBarrier�� 
*/
public class TestCountDownLatch {
	public static void main(String[] args) throws InterruptedException {
		// ��ʼ�ĵ�����
		final CountDownLatch begin = new CountDownLatch(1);
		// �����ĵ�����
		final CountDownLatch end = new CountDownLatch(10);
		// ʮ��ѡ��
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
		//ÿ���̵߳���һ�ξͼ�һ
		begin.countDown();
		//�ȴ��� ����Ϊ0
		end.await();
		System.out.println("Game Over");
		exec.shutdown();
	}
}