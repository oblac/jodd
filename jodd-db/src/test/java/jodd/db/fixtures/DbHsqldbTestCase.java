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

import jodd.db.DbSession;
import jodd.db.pool.CoreConnectionPool;

/**
 * HSLQDB database test case with initial data.
 */
public abstract class DbHsqldbTestCase extends DbTestBase {

	@Override
	protected void setupPool(final CoreConnectionPool cp) {
		cp.setDriver("org.hsqldb.jdbcDriver");
		cp.setUrl("jdbc:hsqldb:mem:test");

		cp.setUser("sa");
		cp.setPassword("");
	}

	@Override
	protected void initDb(final DbSession session) {
		executeUpdate(session, "drop table BOY if exists");
		executeUpdate(session, "drop table GIRL if exists");
		executeUpdate(session, "drop table ENTITY if exists");
		executeUpdate(session, "drop table ENTITY_CHAR if exists");

		String sql = "create table GIRL (" +
				"ID			integer		not null," +
				"NAME		varchar(20)	not null," +
				"SPECIALITY	varchar(20)	null," +
				"primary key (ID)" +
				')';
		executeUpdate(session, sql);

		sql = "create table BOY (" +
				"ID			integer	not null," +
				"GIRL_ID		integer	null," +
				"NAME	varchar(20)	null," +
				"primary key (ID)," +
				"FOREIGN KEY (GIRL_ID) REFERENCES GIRL (ID)" +
				')';
		executeUpdate(session, sql);

		sql = "create table ENTITY (" +
			"ID			integer	not null," +
			"NAME	varchar(20)	null," +
			"VALUE	double  	null," +
			"primary key (ID)" +
			')';
		executeUpdate(session, sql);

		sql = "create table ENTITY_CHAR (" +
				"ID			integer	not null," +
				"NAME	varchar(20)	null," +
				"VALUE	varchar(1) 	null," +
				"primary key (ID)" +
				')';
		executeUpdate(session, sql);
	}

}