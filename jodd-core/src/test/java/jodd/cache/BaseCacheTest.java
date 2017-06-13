package jodd.cache;

import jodd.mutable.MutableInteger;
import org.junit.Test;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public abstract class BaseCacheTest {

	@Test
	public void testThreads_Iterator() throws InterruptedException {
		final int total = 1000;
		final int threads = 200;
		Cache<String, Integer> cache = createCache(total);
		for (int i = 1; i <= total; i++) {
			cache.put(String.valueOf(i), i);
		}

		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		MutableInteger sum = new MutableInteger();
		AtomicInteger taskCount = new AtomicInteger();
		final Object lock = new Object();

		for (int i = 0; i < threads; i++) {
			executorService.submit(() -> {
				Iterator<Integer> iterator = cache.iterator();
				while (iterator.hasNext()) {
					int nextValue = iterator.next();
					synchronized (lock) {
						sum.value += nextValue;
					}
				}
				taskCount.incrementAndGet();
			});
		}

		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.DAYS);

		assertEquals(threads, taskCount.get());
		assertEquals(threads * ((total + 1) * total / 2), sum.get());
	}

	/**
	 * Creates cache instance.
	 */
	protected abstract <K,V> Cache<K,V> createCache(int size);

}
