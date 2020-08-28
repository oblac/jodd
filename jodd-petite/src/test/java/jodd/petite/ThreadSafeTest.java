package jodd.petite;

import jodd.petite.fixtures.data.WeBiz;
import jodd.petite.scope.ProtoScope;
import jodd.petite.scope.SingletonScope;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ThreadSafeTest {

	@Test
	void testThreadSafeSingleton() throws InterruptedException {
		int count = 10000;
		while (count-- > 0) {
			final PetiteContainer pc = new PetiteContainer();
			pc.registerPetiteBean(WeBiz.class, "pojo", SingletonScope.class, WiringMode.AUTOWIRE, false, null);

			final CountDownLatch countDownLatch = new CountDownLatch(2);
			final Object[] beans = new Object[2];
			final Thread thread1 = new Thread(() -> {
				beans[0] = pc.getBean("pojo");
				countDownLatch.countDown();
			});
			final Thread thread2 = new Thread(() -> {
				beans[1] = pc.getBean("pojo");
				countDownLatch.countDown();
			});

			thread1.start();
			thread2.start();

			countDownLatch.await();
			assertEquals(beans[0], beans[1]);
		}
	}

	@Test
	void testThreadSafePrototype() throws InterruptedException {
		int count = 1000;
		while (count-- > 0) {
			final PetiteContainer pc = new PetiteContainer();
			pc.registerPetiteBean(WeBiz.class, "pojo", ProtoScope.class, WiringMode.AUTOWIRE, false, null);

			final CountDownLatch countDownLatch = new CountDownLatch(2);
			final Object[] beans = new Object[2];
			final Thread thread1 = new Thread(() -> {
				beans[0] = pc.getBean("pojo");
				countDownLatch.countDown();
			});
			final Thread thread2 = new Thread(() -> {
				beans[1] = pc.getBean("pojo");
				countDownLatch.countDown();
			});

			thread1.start();
			thread2.start();

			countDownLatch.await();
			assertNotEquals(beans[0], beans[1]);
		}
	}
}
