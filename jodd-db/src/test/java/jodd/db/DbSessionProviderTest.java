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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DbSessionProviderTest extends DbHsqldbTestCase {

	@AfterEach
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
