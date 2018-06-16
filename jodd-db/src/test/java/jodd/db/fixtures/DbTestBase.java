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
package jodd.db.fixtures;

import jodd.db.DbOom;
import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.DbThreadSession;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.db.pool.CoreConnectionPool;
import jodd.db.querymap.DbPropsQueryMap;
import jodd.log.LoggerFactory;
import jodd.system.SystemUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract base test class for working with databases.
 */
public abstract class DbTestBase {

	protected DbOom dbOom;
	protected DbJtxTransactionManager dbtxm;
	protected static CoreConnectionPool cp;

	@BeforeEach
	protected void setUp() throws Exception {
		final DbPropsQueryMap queryMap = new DbPropsQueryMap();

		if (SystemUtil.info().isJavaVersion(9)) {
			queryMap.props().load(this.getClass().getClassLoader().getResourceAsStream("queries.sql.props"));
		}

		LoggerFactory.setLoggerProvider(new TestLoggerProvider());
		if (dbtxm != null) {
			return;
		}

		// create connection pool if not created
		if (cp == null) {
			cp = new CoreConnectionPool();
			setupPool(cp);
			cp.init();
		}

		dbtxm = new DbJtxTransactionManager(cp);

		dbOom = DbOom
			.create()
			.withConnectionProvider(cp)
			.withQueryMap(queryMap)
			.get()
			.connect();

		// initial data
		final DbSession session = new DbSession(cp);
		initDb(session);
		session.closeSession();
	}

	@AfterEach
	protected void tearDown() throws Exception {
		dbtxm.close();
		dbtxm = null;
		DbOom.get().shutdown();
	}

	@AfterAll
	static void tearDownAfterClass() {
		cp.close();
		cp = null;
	}

	/**
	 * Configures connection pool for connectivity.
	 */
	protected abstract void setupPool(CoreConnectionPool cp);

	/**
	 * Initializes database before every test.
	 * It <b>MUST</b> cleanup any existing data or tables first!
	 */
	protected void initDb(final DbSession dbSession) {
	}

	// ---------------------------------------------------------------- helpers

	/**
	 * Creates DB Session.
	 */
	protected DbSession createDbSession() {
		return new DbThreadSession(cp);
	}

	protected int executeUpdate(DbSession session, String sql) {
		return DbQuery.query(session, sql).autoClose().executeUpdate();
	}

	protected void executeUpdate(String sql) {
		DbQuery.query(sql).autoClose().executeUpdate();
	}

	protected long executeCount(DbSession session, String sql) {
		return DbQuery.query(session, sql).autoClose().executeCount();
	}

}
