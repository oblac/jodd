// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class DbSessionProviderTest extends DbHsqldbTestCase {

	@After
	public void tearDown() {
		DbManager.resetAll();
	}

	@Test
	public void testThreadSessionProvider() {
		// set connection provider and thread session manager
		DbManager.getInstance().setConnectionProvider(cp);
		DbManager.getInstance().setSessionProvider(new ThreadDbSessionProvider());

		for (int i = 0; i < 2; i++) {
			// create thread session
			DbSession dbSession = new DbThreadSession();

			// no connection is in use
			assertEquals(0, cp.getConnectionsCount().getBusyCount());
			int availableConnections = cp.getConnectionsCount().getAvailableCount();

			// creates new db query and implicitly open connection
			DbQuery dbQuery = new DbQuery("select 173 from (VALUES(0))");
			long value = dbQuery.executeCount();
			assertEquals(173, value);

			// one connection is in use
			assertEquals(1, cp.getConnectionsCount().getBusyCount());
			assertEquals(availableConnections - 1, cp.getConnectionsCount().getAvailableCount());
			assertTrue(dbQuery.getSession().isSessionOpen());

			// check
			DbThreadSession dbThreadSession = (DbThreadSession) ThreadDbSessionHolder.get();
			assertNotNull(dbThreadSession);
			assertFalse(dbThreadSession.isSessionClosed());
			assertSame(dbSession, dbThreadSession);

			// close db query
			dbQuery.close();

			// session still active
			assertTrue(dbQuery.getSession().isSessionOpen());

			// explicit session closing works
			dbSession.closeSession();

			// check if session is closed and no connection is in use
			assertTrue(dbThreadSession.isSessionClosed());
			assertEquals(0, cp.getConnectionsCount().getBusyCount());
			assertEquals(availableConnections, cp.getConnectionsCount().getAvailableCount());
		}
	}
}
