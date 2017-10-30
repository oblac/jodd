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

import jodd.db.oom.DbOomConfig;
import jodd.db.oom.sqlgen.SqlGenConfig;

public class JoddDbDefaults {

	// ---------------------------------------------------------------- debug

	protected boolean debug = false;

	public boolean isDebug() {
		return debug;
	}

	/**
	 * Enables debug mode.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	// ---------------------------------------------------------------- tx

	protected DbTransactionMode transactionMode = new DbTransactionMode();

	public DbTransactionMode getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(DbTransactionMode transactionMode) {
		this.transactionMode = transactionMode;
	}

	// ---------------------------------------------------------------- query

	protected DbQueryConfig queryConfig = new DbQueryConfig();

	public DbQueryConfig getQueryConfig() {
		return queryConfig;
	}

	public void setQueryConfig(DbQueryConfig queryConfig) {
		this.queryConfig = queryConfig;
	}


	// ---------------------------------------------------------------- oom

	protected DbOomConfig dbOomConfig = new DbOomConfig();

	public DbOomConfig getDbOomConfig() {
		return dbOomConfig;
	}

	public void setDbOomConfig(DbOomConfig dbOomConfig) {
		this.dbOomConfig = dbOomConfig;
	}

	// ----------------------------------------------------------------

	private SqlGenConfig sqlGenConfig = new SqlGenConfig();

	/**
	 * Returns {@link SqlGenConfig}.
	 */
	public SqlGenConfig getSqlGenConfig() {
		return sqlGenConfig;
	}

	public void setSqlGenConfig(SqlGenConfig sqlGenConfig) {
		this.sqlGenConfig = sqlGenConfig;
	}
}
