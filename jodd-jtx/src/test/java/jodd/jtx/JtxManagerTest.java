// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.jtx;

import jodd.exception.UncheckedException;
import jodd.jtx.fixtures.WorkResourceManager;
import jodd.jtx.fixtures.WorkSession;
import jodd.jtx.worker.LeanJtxWorker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class JtxManagerTest {

	private JtxTransactionManager createManager() {
		JtxTransactionManager jtxManager = new JtxTransactionManager();
		jtxManager.registerResourceManager(new WorkResourceManager());
		return jtxManager;
	}

	private LeanJtxWorker createWorker() {
		return new LeanJtxWorker(createManager());
	}

	// ---------------------------------------------------------------- ro

	@Test
	public void testReadOnly() {
		JtxTransactionManager manager = createManager();
		JtxTransaction jtx = manager.requestTransaction(new JtxTransactionMode().propagationRequired().readOnly(true));
		WorkSession work = jtx.requestResource(WorkSession.class);

		WorkSession work2 = jtx.requestResource(WorkSession.class);
		assertSame(work2, work);

		try {
			work.writeValue("new value");
			fail("error");
		} catch (UncheckedException ignored) {
		}

		jtx.commit();
		manager.close();
	}

	// ---------------------------------------------------------------- rollback

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
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
