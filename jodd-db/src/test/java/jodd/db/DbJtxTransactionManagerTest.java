// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.jtx.DbJtxSessionProvider;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DbJtxTransactionManagerTest extends DbHsqldbTestCase {

	@Test
	public void testSessionProvider() {
		JtxTransactionManager jtxManager = new DbJtxTransactionManager(cp);
		DbJtxSessionProvider sessionProvider = new DbJtxSessionProvider(jtxManager, new JtxTransactionMode().propagationSupports());

		assertEquals(0, jtxManager.totalTransactions());

		DbSession dbSession = sessionProvider.getDbSession();
		assertNotNull(dbSession);
		assertEquals(1, jtxManager.totalTransactions());

		// transaction is committed and closed
		jtxManager.getTransaction().commit();
		assertEquals(0, jtxManager.totalTransactions());

		// session is closed as well
		assertEquals(0, cp.getConnectionsCount().getBusyCount());

	}
}
