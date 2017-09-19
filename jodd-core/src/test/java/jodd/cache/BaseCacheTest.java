package jodd.cache;

import jodd.mutable.MutableInteger;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseCacheTest {

	/**
	 * Creates cache instance.
	 */
	protected abstract <K,V> Cache<K,V> createCache(int size);

	@Test
	public void testSnapshot() {
		Cache<String, Integer> cache = createCache(3);

		cache.put("1", 1);
		assertEquals(1, cache.snapshot().size());
		assertEquals(1, cache.snapshot().get("1").intValue());

		cache.put("2", 2);
		cache.put("3", 3);
		assertEquals(3, cache.snapshot().size());

		cache.put("4", 4);
		assertEquals(3, cache.snapshot().size());
	}

	@Test
	public void testConcurrency() throws InterruptedException {
		final int total = 100000;
		final int threads = 100;

		Cache<Integer, String> cache = createCache(total);
		ExecutorService executorService = Executors.newFixedThreadPool(threads);

		final LongAdder taskCount = new LongAdder();
		final Random random = new Random();

		for (int i = 0; i < total; i++) {
			executorService.submit(() -> {
				cache.put(random.nextInt(10), "value", random.nextInt(50));
				cache.get(random.nextInt(10));
				taskCount.increment();
			});
		}

		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.DAYS);

		assertEquals(total, taskCount.intValue());
	}

}
