// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.jtx.DbJtxSessionProvider;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DbJtxTransactionManagerTest extends DbHsqldbTestCase {

	@After
	public void tearDown() {
		DbManager.resetAll();
	}

	@Test
	public void testSessionProvider() {
		// prepare
		JtxTransactionManager jtxManager = new DbJtxTransactionManager(cp);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(jtxManager);
		DbManager.getInstance().setSessionProvider(sessionProvider);

		for (int i = 0; i < 2; i++) {

			// start, 0 transactions, no session
			assertEquals(0, jtxManager.totalTransactions());

			// start transaction
			jtxManager.requestTransaction(new JtxTransactionMode());

			// get session from provider!
			DbSession dbSession = sessionProvider.getDbSession();
			assertNotNull(dbSession);

			// transaction started, but connection not yet fetched as it is not used yet
			assertEquals(1, jtxManager.totalTransactions());
			assertEquals(0, cp.getConnectionsCount().getBusyCount());

			// same session as it is the same transaction
			DbSession dbSession2 = sessionProvider.getDbSession();
			assertNotNull(dbSession2);
			assertSame(dbSession, dbSession2);

			// create query, session is get from provider, the very same one
			DbQuery dbQuery = new DbQuery("SELECT 173 FROM (VALUES(0))");
			long value = dbQuery.executeCount();
			assertEquals(173, value);
			assertSame(dbSession, dbQuery.getSession());

			// transaction still active, connection still in use
			assertEquals(1, jtxManager.totalTransactions());
			assertEquals(1, cp.getConnectionsCount().getBusyCount());

			// close query
			dbQuery.close();

			// transaction still active, connection still in use (!)
			// since session is still active
			assertEquals(1, jtxManager.totalTransactions());
			assertEquals(1, cp.getConnectionsCount().getBusyCount());
			assertTrue(!dbQuery.getSession().isSessionClosed());

			// commit transaction...
			jtxManager.getTransaction().commit();

			// no transaction
			assertEquals(0, jtxManager.totalTransactions());
			// session is closed
			assertTrue(dbSession.isSessionClosed());
			// connection is returned
			assertEquals(0, cp.getConnectionsCount().getBusyCount());
		}
	}
}
