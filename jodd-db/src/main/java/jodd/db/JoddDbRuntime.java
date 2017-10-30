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

import jodd.db.connection.ConnectionProvider;
import jodd.db.oom.DbEntityManager;
import jodd.db.oom.JoinHintResolver;
import jodd.db.querymap.QueryMap;

public class JoddDbRuntime {

	protected ConnectionProvider connectionProvider = null;
	protected DbSessionProvider sessionProvider = new ThreadDbSessionProvider();
	protected QueryMap queryMap;
	protected JoinHintResolver hintResolver = new JoinHintResolver();
	protected DbEntityManager dbEntityManager = new DbEntityManager();

	/**
	 * Returns hints resolver.
	 */
	public JoinHintResolver hintResolver() {
		return hintResolver;
	}

	/**
	 * Specifies the hint resolver.
	 */
	public void hintResolver(JoinHintResolver hintResolver) {
		this.hintResolver = hintResolver;
	}


	public DbEntityManager dbEntityManager() {
		return dbEntityManager;
	}

	public void dbEntityManager(DbEntityManager dbEntityManager) {
		this.dbEntityManager = dbEntityManager;
	}


	public ConnectionProvider connectionProvider() {
		return connectionProvider;
	}

	/**
	 * Sets connection provider.
	 */
	public void connectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	public DbSessionProvider sessionProvider() {
		return sessionProvider;
	}

	/**
	 * Sets default session provider.
	 */
	public void sessionProvider(DbSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	/**
	 * Returns {@link jodd.db.querymap.QueryMap} instance. May be <code>null</code>.
	 */
	public QueryMap queryMap() {
		return queryMap;
	}

	public void queryMap(QueryMap queryMap) {
		this.queryMap = queryMap;
	}
}
