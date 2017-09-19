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

package jodd.joy.db;

import jodd.db.DbQuery;
import jodd.db.DbSession;
import jodd.db.jtx.DbJtxTransactionManager;
import jodd.db.pool.CoreConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class DbHsqldbTestCase {

	protected DbJtxTransactionManager dbtxm;
	protected CoreConnectionPool cp;

	@BeforeEach
	public void setUp() throws Exception {
		cp = new CoreConnectionPool();
		cp.setDriver("org.hsqldb.jdbcDriver");
		cp.setUrl("jdbc:hsqldb:mem:test");

		cp.setUser("sa");
		cp.setPassword("");
		cp.init();
		dbtxm = new DbJtxTransactionManager(cp);

		// initial data
		DbSession session = new DbSession(cp);

		executeUpdate(session, "drop table BOY if exists");
		executeUpdate(session, "drop table GIRL if exists");

		String sql = "create table GIRL (" +
				"ID			integer		not null," +
				"NAME		varchar(20)	not null," +
				"SPECIALITY	varchar(20)	null," +
				"primary key (ID)" +
				')';

		executeUpdate(session, sql);

		sql = "create table BOY (" +
				"ID			integer	not null," +
				"GIRL_ID	integer	null," +
				"NAME	varchar(20)	null," +
				"primary key (ID)," +
				"FOREIGN KEY (GIRL_ID) REFERENCES GIRL (ID)" +
				')';

		executeUpdate(session, sql);
		session.closeSession();
	}

	@AfterEach
	public void tearDown() throws Exception {
		dbtxm.close();
//		cp.close();
		dbtxm = null;
	}

	// ---------------------------------------------------------------- helpers

	protected int executeUpdate(DbSession session, String s) {
		return new DbQuery(session, s).autoClose().executeUpdate();
	}

	protected void executeUpdate(String sql) {
		new DbQuery(sql).autoClose().executeUpdate();
	}

	protected long executeCount(DbSession session, String s) {
		return new DbQuery(session, s).autoClose().executeCount();
	}


}
