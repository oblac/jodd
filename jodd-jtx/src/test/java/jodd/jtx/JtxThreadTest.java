package jodd.jtx;

import jodd.jtx.fixtures.WorkResourceManager;
import jodd.jtx.fixtures.WorkSession;
import jodd.jtx.worker.LeanJtxWorker;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static jodd.jtx.JtxPropagationBehavior.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JtxThreadTest {

	public static final int COUNT = 2_000_000;

	@Test
	void testMultithreadsAndJtx() throws InterruptedException {
		JtxTransactionManager jtxManager = new JtxTransactionManager();
		WorkResourceManager workResourceManager = new WorkResourceManager();

		jtxManager.registerResourceManager(workResourceManager);
		LeanJtxWorker x = new LeanJtxWorker(jtxManager);

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(200);
		CountDownLatch latch = new CountDownLatch(COUNT);

		for (int i = 0; i < COUNT; i++) {
			executor.execute(() -> {
				JtxTransaction jtx = askForTx(x);
				WorkSession work = jtx.requestResource(WorkSession.class);

				JtxTransaction jtx2 = askForTx(x);

				work.writeValue("X");

				byeTx(x, jtx2);
				byeTx(x, jtx);

				latch.countDown();
			});
		}

		assertTrue(latch.await(5, TimeUnit.SECONDS));
		assertEquals(0, workResourceManager.txno.get());
	}

	private JtxTransaction askForTx(LeanJtxWorker w) {
		return w.maybeRequestTransaction(new JtxTransactionMode(PROPAGATION_REQUIRED, false), this);
	}

	private void byeTx(LeanJtxWorker w, JtxTransaction jtxTransaction) {
		w.maybeCommitTransaction(jtxTransaction);
	}
}
