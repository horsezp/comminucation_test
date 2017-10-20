package com.leo.zk;

public class Test {

	public static void main(String[] args) {
		DistributedLock lock = new DistributedLock("10.1.126.87:2181", "lock");
		lock.lock();
		// ¹²Ïí×ÊÔ´
		if (lock != null)
			lock.unlock();

	}

}
