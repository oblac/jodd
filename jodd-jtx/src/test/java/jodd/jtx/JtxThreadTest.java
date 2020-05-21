package jodd.jtx;

import jodd.jtx.fixtures.WorkResourceManager;
import jodd.jtx.fixtures.WorkSession;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static jodd.jtx.JtxPropagationBehavior.*;
import static org.junit.jupiter.api.Assertions.fail;

class JtxThreadTest {

	@Test
	void testMultithreadsAndJtx() {
		JtxTransactionManager jtxm = createManager();

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
		CountDownLatch latch = new CountDownLatch(10000);

		for (int i = 0; i < 10000; i++) {
			executor.execute(() -> {
				JtxTransaction jtx = askForTx(jtxm);
				WorkSession work = jtx.requestResource(WorkSession.class);

				work.writeValue("X");

				jtx.commit();
				latch.countDown();
			});
		}

		try {
			latch.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			fail(e);
		}
		jtxm.close();
	}


	private JtxTransactionManager createManager() {
		JtxTransactionManager jtxManager = new JtxTransactionManager();
		jtxManager.registerResourceManager(new WorkResourceManager());
		return jtxManager;
	}

	private JtxTransaction askForTx(JtxTransactionManager jtxm) {
		return jtxm.requestTransaction(new JtxTransactionMode(PROPAGATION_REQUIRED, false));
	}
}
