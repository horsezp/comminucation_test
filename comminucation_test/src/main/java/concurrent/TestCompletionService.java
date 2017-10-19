package concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*Java�Ĳ������CompletionService�����������ֳ���Ҫ��
 * �ýӿ���������Ҫ������submit()��take()��
 * submit�����ύһ��runnable����callable��
 * һ����ύ��һ���̳߳ش���
 * ��take����ȡ���Ѿ�ִ�����runnable����callableʵ����Future����
 * ���û������Ҫ��ģ��͵ȴ��ˡ� CompletionService����һ����Ӧ�ķ���poll��
 * �÷�����take���ƣ�ֻ�ǲ���ȴ������û������Ҫ�󣬾ͷ���null���� 
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
			//take ������ �� poll()�Ͳ�������
			Future<String> task = serv.take();
			//����������ǵ��Ѿ��������߳� �����get�����Ͳ���������
			String img = task.get();
			System.out.println(img);
		}
		System.out.println("End");
		// �ر��̳߳�
		exec.shutdown();
	}
}