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

package jodd.db;

import jodd.db.fixtures.DbHsqldbTestCase;
import jodd.db.jtx.DbJtxSessionProvider;
import jodd.jtx.JtxException;
import jodd.jtx.JtxPropagationBehavior;
import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionMode;
import jodd.jtx.worker.LeanJtxWorker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DbPropagationTest extends DbHsqldbTestCase {

	private static final String CTX_1 = "ctx #1";
	private static final String CTX_2 = "ctx #2";

	// ---------------------------------------------------------------- required

	private JtxTransactionMode required() {
		return new JtxTransactionMode(JtxPropagationBehavior.PROPAGATION_REQUIRED, false);
	}

	@Test
	void testRequired() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: required - commit
		JtxTransaction jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertTrue(jtx.isActive());
		assertNotNull(jtx);
		DbSession session = sessionProvider.getDbSession();

		executeUpdate(session, "insert into GIRL values(1, 'Sophia', null)");
		assertTrue(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());

		// session #2: required - rollback
		jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertNotNull(jtx);
		session = sessionProvider.getDbSession();
		executeUpdate(session, "insert into GIRL values(2, 'Gloria', null)");
		assertTrue(worker.markOrRollbackTransaction(jtx, null));

		// test
		session = new DbSession(cp);
		assertEquals(1, executeCount(session, "select count(*) from GIRL where id = 1"));
		assertEquals(0, executeCount(session, "select count(*) from GIRL where id = 2"));
		session.closeSession();
	}

	@Test
	void testRequiredToRequiredCommit() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: required
		JtxTransaction jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertNotNull(jtx);
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertTrue(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());


		// session #2: inner, required
		JtxTransaction jtx2 = worker.maybeRequestTransaction(required(), CTX_2);
		assertNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertFalse(worker.maybeCommitTransaction(jtx2));
		assertTrue(jtx.isActive());

		// session #1: commit
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}

	@Test
	void testRequiredToRequiredRollback() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: required
		JtxTransaction jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertNotNull(jtx);
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertTrue(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());


		// session #2: inner, required
		JtxTransaction jtx2 = worker.maybeRequestTransaction(required(), CTX_2);
		assertNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertFalse(worker.markOrRollbackTransaction(jtx2, null));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isRollbackOnly());

		// session #1: commit
		assertTrue(worker.markOrRollbackTransaction(jtx, null));
		assertFalse(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertTrue(jtx.isRolledback());

		// test
		session1 = new DbSession(cp);
		assertEquals(0, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(0, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}

	@Test
	void testSupportsToRequiredCommit() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: supports
		JtxTransaction jtx = worker.maybeRequestTransaction(supports(), CTX_1);
		assertNotNull(jtx);
		assertFalse(jtx.isActive());
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertFalse(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertTrue(jtx.isNoTransaction());


		// session #2: inner, required
		JtxTransaction jtx2 = worker.maybeRequestTransaction(required(), CTX_2);
		assertNotNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertNotSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertTrue(worker.maybeCommitTransaction(jtx2));
		assertFalse(jtx2.isActive());

		// session #1: rollback
		assertTrue(worker.markOrRollbackTransaction(jtx, null));
		assertFalse(jtx.isActive());
		assertFalse(jtx.isCommitted());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}


	// ---------------------------------------------------------------- supports

	private JtxTransactionMode supports() {
		return new JtxTransactionMode(JtxPropagationBehavior.PROPAGATION_SUPPORTS, false);
	}

	@Test
	void testSupportsNone() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: supports - commit
		JtxTransaction jtx = worker.maybeRequestTransaction(supports(), CTX_1);
		assertNotNull(jtx);
		DbSession session = sessionProvider.getDbSession();

		executeUpdate(session, "insert into GIRL values(1, 'Sophia', null)");
		assertFalse(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertTrue(jtx.isNoTransaction());
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());

		// session #2: required - rollback
		jtx = worker.maybeRequestTransaction(supports(), CTX_2);
		assertNotNull(jtx);
		session = sessionProvider.getDbSession();
		executeUpdate(session, "insert into GIRL values(2, 'Gloria', null)");
		assertTrue(worker.markOrRollbackTransaction(jtx, null));

		// test
		session = new DbSession(cp);
		assertEquals(1, executeCount(session, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session, "select count(*) from GIRL where id = 2"));
		session.closeSession();
	}

	@Test
	void testRequiredToSupportsCommit() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: required
		JtxTransaction jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertNotNull(jtx);
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertTrue(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());


		// session #2: inner, supports
		JtxTransaction jtx2 = worker.maybeRequestTransaction(supports(), CTX_2);
		assertNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertFalse(worker.maybeCommitTransaction(jtx2));
		assertTrue(jtx.isActive());

		// session #1: commit
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}


	@Test
	void testSupportsToSupportsCommit() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: supports
		JtxTransaction jtx = worker.maybeRequestTransaction(supports(), CTX_1);
		assertNotNull(jtx);
		assertFalse(jtx.isActive());
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertFalse(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertTrue(jtx.isNoTransaction());


		// session #2: inner, supports
		JtxTransaction jtx2 = worker.maybeRequestTransaction(supports(), CTX_2);
		assertNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertFalse(worker.maybeCommitTransaction(jtx2));
		assertFalse(jtx.isActive());

		// session #1: commit
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}

	// ---------------------------------------------------------------- not supported

	private JtxTransactionMode notSupported() {
		return new JtxTransactionMode(JtxPropagationBehavior.PROPAGATION_NOT_SUPPORTED, false);
	}

	@Test
	void testNotSupported() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: not supported - commit
		JtxTransaction jtx = worker.maybeRequestTransaction(notSupported(), CTX_1);
		assertNotNull(jtx);
		DbSession session = sessionProvider.getDbSession();

		executeUpdate(session, "insert into GIRL values(1, 'Sophia', null)");
		assertFalse(jtx.isActive());
		assertTrue(jtx.isNoTransaction());
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());

		// session #2: not supported - rollback
		jtx = worker.maybeRequestTransaction(notSupported(), CTX_2);
		assertNotNull(jtx);
		session = sessionProvider.getDbSession();
		assertFalse(jtx.isActive());
		assertTrue(jtx.isNoTransaction());
		executeUpdate(session, "insert into GIRL values(2, 'Gloria', null)");
		assertTrue(worker.markOrRollbackTransaction(jtx, null));

		// test
		session = new DbSession(cp);
		assertEquals(1, executeCount(session, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session, "select count(*) from GIRL where id = 2"));
		session.closeSession();
	}

/*
	void testRequiredToNotSupported() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: required
		JtxTransaction jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertNotNull(jtx);
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertTrue(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());


		// session #2: inner, required
		JtxTransaction jtx2 = worker.maybeRequestTransaction(notSupported(), CTX_2);
		assertNotNull(jtx2);
		assertFalse(jtx2.isActive());
		assertTrue(jtx2.isNoTransaction());
		DbSession session2 = sessionProvider.getDbSession();
		assertNotSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertTrue(worker.markOrRollbackTransaction(jtx2, null));
		assertFalse(jtx2.isActive());
		assertTrue(jtx2.isRolledback());
		assertFalse(jtx2.isStartAsActive());

		// session #1: commit
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());
		assertFalse(jtx.isRolledback());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}

*/

	// ---------------------------------------------------------------- never

	private JtxTransactionMode never() {
		return new JtxTransactionMode(JtxPropagationBehavior.PROPAGATION_NEVER, false);
	}

	@Test
	void testRequiredToNever() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: required
		JtxTransaction jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertNotNull(jtx);
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertTrue(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());


		// session #2: inner, never
		try {
			worker.maybeRequestTransaction(never(), CTX_2);
			fail("error");
		} catch (JtxException ignore) {
		}
	}

	@Test
	void testSupportsToNeverCommit() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: supports
		JtxTransaction jtx = worker.maybeRequestTransaction(supports(), CTX_1);
		assertNotNull(jtx);
		assertFalse(jtx.isActive());
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertFalse(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertTrue(jtx.isNoTransaction());


		// session #2: inner, supports
		JtxTransaction jtx2 = worker.maybeRequestTransaction(never(), CTX_2);
		assertNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertFalse(worker.maybeCommitTransaction(jtx2));
		assertFalse(jtx.isActive());

		// session #1: commit
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}


	// ---------------------------------------------------------------- requires new

	private JtxTransactionMode requiredNew() {
		return new JtxTransactionMode(JtxPropagationBehavior.PROPAGATION_REQUIRES_NEW, false);
	}

/*
	void testRequiredToRequiredNewCommit() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: required
		JtxTransaction jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertNotNull(jtx);
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertTrue(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());


		// session #2: inner, required new
		// important - have to be in different context!
		JtxTransaction jtx2 = worker.maybeRequestTransaction(requiredNew(), CTX_2);
		assertNotNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertNotSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertTrue(worker.maybeCommitTransaction(jtx2));
		assertFalse(jtx2.isActive());
		assertTrue(jtx2.isCommitted());

		// session #1: commit
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}
*/

/*
	void testRequiredToRequiredNewRollback() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: required
		JtxTransaction jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertNotNull(jtx);
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertTrue(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());


		// session #2: inner, required new
		// important - have to be in different context!
		JtxTransaction jtx2 = worker.maybeRequestTransaction(requiredNew(), CTX_2);
		assertFalse(worker.getTransactionManager().isIgnoreScope());
		assertNotNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertNotSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertTrue(worker.markOrRollbackTransaction(jtx2, null));
		assertFalse(jtx2.isActive());
		assertFalse(jtx2.isCommitted());
		assertTrue(jtx2.isRolledback());

		// session #1: commit
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(0, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}
*/

	@Test
	void testSupportsToRequiresNewCommit() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: supports
		JtxTransaction jtx = worker.maybeRequestTransaction(supports(), CTX_1);
		assertNotNull(jtx);
		assertFalse(jtx.isActive());
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertFalse(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertTrue(jtx.isNoTransaction());


		// session #2: inner, required
		JtxTransaction jtx2 = worker.maybeRequestTransaction(requiredNew(), CTX_2);
		assertNotNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertNotSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertTrue(worker.maybeCommitTransaction(jtx2));
		assertFalse(jtx2.isActive());

		// session #1: rollback
		assertTrue(worker.markOrRollbackTransaction(jtx, null));
		assertFalse(jtx.isActive());
		assertFalse(jtx.isCommitted());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}


	// ---------------------------------------------------------------- mandatory

	private JtxTransactionMode mandatory() {
		return new JtxTransactionMode(JtxPropagationBehavior.PROPAGATION_MANDATORY, false);
	}

	@Test
	void testRequiredToMandatoryCommit() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: required
		JtxTransaction jtx = worker.maybeRequestTransaction(required(), CTX_1);
		assertNotNull(jtx);
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertTrue(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertFalse(jtx.isNoTransaction());


		// session #2: inner, mandatory
		JtxTransaction jtx2 = worker.maybeRequestTransaction(mandatory(), CTX_2);
		assertNull(jtx2);
		DbSession session2 = sessionProvider.getDbSession();
		assertSame(session1, session2);
		executeUpdate(session2, "insert into GIRL values(2, 'Gloria', null)");
		assertFalse(worker.maybeCommitTransaction(jtx2));
		assertTrue(jtx.isActive());

		// session #1: commit
		assertTrue(worker.maybeCommitTransaction(jtx));
		assertFalse(jtx.isActive());
		assertTrue(jtx.isCommitted());

		// test
		session1 = new DbSession(cp);
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 1"));
		assertEquals(1, executeCount(session1, "select count(*) from GIRL where id = 2"));
		session1.closeSession();
	}

	@Test
	void testSupportsToMandatory() {
		LeanJtxWorker worker = new LeanJtxWorker(dbtxm);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(worker.getTransactionManager());

		// session #1: supports
		JtxTransaction jtx = worker.maybeRequestTransaction(supports(), CTX_1);
		assertNotNull(jtx);
		assertFalse(jtx.isActive());
		DbSession session1 = sessionProvider.getDbSession();

		executeUpdate(session1, "insert into GIRL values(1, 'Sophia', null)");
		assertFalse(jtx.isActive());
		assertFalse(jtx.isCommitted());
		assertTrue(jtx.isNoTransaction());


		// session #2: inner, mandatory
		try {
			worker.maybeRequestTransaction(mandatory(), CTX_2);
			fail("error");
		} catch (JtxException ignore) {
		}
	}

}
