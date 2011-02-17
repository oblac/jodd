// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx;

import jodd.exception.UncheckedException;
import jodd.jtx.data.WorkResourceManager;
import jodd.jtx.data.WorkSession;
import jodd.jtx.worker.LeanJtxWorker;
import junit.framework.TestCase;

public class JtxManagerTest extends TestCase {

	private JtxTransactionManager createManager() {
		JtxTransactionManager jtxManager = new JtxTransactionManager();
		jtxManager.registerResourceManager(new WorkResourceManager());
		return jtxManager;
	}

	private LeanJtxWorker createWorker() {
		return new LeanJtxWorker(createManager());
	}

	// ---------------------------------------------------------------- ro

	public void testReadOnly() {
		JtxTransactionManager manager = createManager();
		JtxTransaction jtx = manager.requestTransaction(new JtxTransactionMode().propagationRequired().readOnly(true));
		WorkSession work = jtx.requestResource(WorkSession.class);

		WorkSession work2 = jtx.requestResource(WorkSession.class);
		assertSame(work2, work);

		try {
			work.writeValue("new value");
			fail();
		} catch (UncheckedException ignored) {
		}

		jtx.commit();
		manager.close();
	}

	// ---------------------------------------------------------------- rollback

	public void testRollback() {
		JtxTransactionManager manager = createManager();
		JtxTransaction jtx = manager.requestTransaction(new JtxTransactionMode().propagationRequired().readOnly(false));
		WorkSession work = jtx.requestResource(WorkSession.class);

		work.writeValue("new value");
		jtx.rollback();
		assertFalse(work.readValue().equals("new value"));

		manager.close();

	}


	// ---------------------------------------------------------------- required

	public void testPropagationRequired() {

		JtxTransactionManager manager = createManager();

		JtxTransaction jtx1 = manager.requestTransaction(new JtxTransactionMode().propagationRequired().readOnly(false));
		WorkSession work1 = jtx1.requestResource(WorkSession.class);
		assertEquals(1, manager.totalTransactions());
		work1.writeValue("one");
		assertEquals("[1] one", work1.readValue());

		JtxTransaction jtx2 = manager.requestTransaction(new JtxTransactionMode().propagationRequired().readOnly(false));
		assertEquals(1, manager.totalTransactions());
		assertSame(jtx2, jtx1);

		WorkSession work2 = jtx2.requestResource(WorkSession.class);
		assertSame(work2, work1);
		work2.writeValue("two");
		assertEquals("[1] two", work2.readValue());
		//jtx2.commit();

		work1.writeValue("three");
		assertEquals("[1] three", work1.readValue());

		jtx1.commit();

		assertTrue(jtx1.isCommitted());
	}

	public void testPropagationRequiredWithWorker() {

		LeanJtxWorker worker = createWorker();

		JtxTransaction jtx1 = worker.maybeRequestTransaction(new JtxTransactionMode().propagationRequired().readOnly(false), null);
		WorkSession work1 = jtx1.requestResource(WorkSession.class);
		assertEquals(1, worker.getTransactionManager().totalTransactions());
		work1.writeValue("one");
		assertEquals("[1] one", work1.readValue());

		JtxTransaction jtx2 = worker.maybeRequestTransaction(new JtxTransactionMode().propagationRequired().readOnly(false), null);
		assertEquals(1, worker.getTransactionManager().totalTransactions());
		assertNull(jtx2);

		WorkSession work2 = jtx1.requestResource(WorkSession.class);
		assertSame(work2, work1);
		work2.writeValue("two");
		assertEquals("[1] two", work2.readValue());
		assertFalse(worker.maybeCommitTransaction(jtx2));

		work1.writeValue("three");
		assertEquals("[1] three", work1.readValue());
		assertTrue(worker.maybeCommitTransaction(jtx1));
	}

	// ---------------------------------------------------------------- supports

	public void testPropagationSupports() {

		JtxTransactionManager manager = createManager();

		JtxTransaction jtx1 = manager.requestTransaction(new JtxTransactionMode().propagationSupports().readOnly(false));
		WorkSession work1 = jtx1.requestResource(WorkSession.class);
		assertEquals(1, manager.totalTransactions());
		work1.writeValue("one");
		assertEquals("one", work1.readValue());

		jtx1.commit();

		assertTrue(jtx1.isCommitted());
	}


	// ---------------------------------------------------------------- required

	public void testPropagationRequiresNew() {

		JtxTransactionManager manager = createManager();

		JtxTransaction jtx1 = manager.requestTransaction(new JtxTransactionMode().propagationRequired().readOnly(false));
		WorkSession work1 = jtx1.requestResource(WorkSession.class);
		assertEquals(1, manager.totalTransactions());
		work1.writeValue("one");
		assertEquals("[1] one", work1.readValue());

		JtxTransaction jtx2 = manager.requestTransaction(new JtxTransactionMode().propagationRequiresNew().readOnly(false));
		assertEquals(2, manager.totalTransactions());
		assertNotSame(jtx2, jtx1);

		WorkSession work2 = jtx2.requestResource(WorkSession.class);
		assertNotSame(work2, work1);
		work2.writeValue("two");
		assertEquals("[2] two", work2.readValue());
		jtx2.commit();

		work1.writeValue("three");
		assertEquals("[1] three", work1.readValue());

		jtx1.commit();

		assertTrue(jtx1.isCommitted());

		assertEquals("[1] three", WorkSession.getPersistedValue());
	}


}
