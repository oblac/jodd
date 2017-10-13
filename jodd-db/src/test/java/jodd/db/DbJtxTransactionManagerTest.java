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
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DbJtxTransactionManagerTest extends DbHsqldbTestCase {

	@AfterEach
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
