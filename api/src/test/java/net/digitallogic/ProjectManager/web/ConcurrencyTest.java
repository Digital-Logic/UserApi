package net.digitallogic.ProjectManager.web;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcurrencyTest {

	public static class Counter {
		private int i = 0;
		private AtomicInteger b = new AtomicInteger();
		private final Lock lock = new ReentrantLock();

		public void increment() {

			lock.lock();
			int fab = getFab(25);
			i++;
			lock.unlock();

		}

		public int getI() {
			return i;
		}
	}

	public static class FindDuplicates {
		Map<Character, Integer> occurrences = new HashMap<>();

		String str = "ABCD ABCD ABCD";

		public void doIt() {

		}

	}

	public static int getFab(int n) {
		if (n <2)
			return n;
		return getFab(n-1) + getFab(n-2);
	}

	@Test
	public void incrementTest() {
		Counter counter = new Counter();
		counter.increment();
		counter.increment();

		System.out.println("Counter: " + counter.getI());
	}

	@Test
	public void concurrentCounterTest() throws InterruptedException {
		Counter counter = new Counter();
		int threadSize = 1000;

		final CountDownLatch callingLatch = new CountDownLatch(1);
		final CountDownLatch completedLatch = new CountDownLatch(threadSize);

		List<Thread> threads = Stream.generate(() -> new Thread(new Worker(counter, callingLatch, completedLatch)))
				.limit(threadSize)
				.collect(Collectors.toList());

		threads.forEach(Thread::start);

		long startTime = new Date().getTime();

		// Start workers
		callingLatch.countDown();

		completedLatch.await();

		long dif = new Date().getTime() - startTime;
		System.out.println("Counter: " + counter.getI() + ", processing took: " + dif);

		assertThat(counter.getI()).isEqualTo(threadSize);
	}

	public static class Worker implements Runnable {
		private final Counter counter;
		private final CountDownLatch callingLatch;
		private final CountDownLatch completedLatch;

		public Worker(Counter counter, CountDownLatch callingLatch, CountDownLatch completedLatch) {
			this.counter = counter;
			this.callingLatch = callingLatch;
			this.completedLatch = completedLatch;
		}

		@Override
		public void run() {
			try {
				callingLatch.await();
				counter.increment();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			} finally {
				completedLatch.countDown();
			}
		}
	}
}
