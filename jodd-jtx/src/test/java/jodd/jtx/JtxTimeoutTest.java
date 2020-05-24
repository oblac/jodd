package jodd.jtx;

import jodd.jtx.fixtures.WorkResourceManager;
import jodd.jtx.fixtures.WorkSession;
import jodd.jtx.worker.LeanJtxWorker;
import jodd.util.ThreadUtil;
import org.junit.jupiter.api.Test;

import static jodd.jtx.JtxIsolationLevel.ISOLATION_DEFAULT;
import static jodd.jtx.JtxPropagationBehavior.PROPAGATION_REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JtxTimeoutTest {

	@Test
	void testTimeOutAndNewTx() {
		final JtxTransactionManager jtxManager = new JtxTransactionManager();
		final WorkResourceManager workResourceManager = new WorkResourceManager();

		jtxManager.registerResourceManager(workResourceManager);
		final LeanJtxWorker x = new LeanJtxWorker(jtxManager);

		final JtxTransaction jtx = askForTx(x, 1);
		final WorkSession work = jtx.requestResource(WorkSession.class);

		work.writeValue("X");
		ThreadUtil.sleep(1100);

		byeTx(x, jtx);

		// new transaction, the previous one should be cleared!
		final JtxTransaction newJtx = askForTx(x, 1);
		final WorkSession newWork = newJtx.requestResource(WorkSession.class);
		assertNotNull(newJtx);
		byeTx(x, newJtx);

		assertEquals(0, workResourceManager.txno.get());
	}

	private JtxTransaction askForTx(final LeanJtxWorker w, final int timeout) {
		return w.maybeRequestTransaction(new JtxTransactionMode(PROPAGATION_REQUIRED, ISOLATION_DEFAULT, false, timeout), this);
	}

	private void byeTx(final LeanJtxWorker w, final JtxTransaction jtxTransaction) {
		try {
			w.maybeCommitTransaction(jtxTransaction);
		}
		catch (final Exception cause) {
			w.markOrRollbackTransaction(jtxTransaction, cause);
		}
	}
}
