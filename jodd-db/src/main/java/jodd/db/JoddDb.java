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

import jodd.Jodd;
import jodd.db.connection.ConnectionProvider;
import jodd.db.oom.DbEntityManager;
import jodd.db.oom.JoinHintResolver;
import jodd.db.pool.CoreConnectionPool;
import jodd.db.querymap.EmptyQueryMap;
import jodd.db.querymap.QueryMap;

import java.util.Objects;

/**
 * Jodd DB module.
 */
public class JoddDb {

	private static final JoddDb instance = new JoddDb();

	/**
	 * Returns the module instance.
	 */
	public static JoddDb get() {
		return instance;
	}

	static {
		Jodd.initModule();
	}

	public static void init() {}

	// ---------------------------------------------------------------- instance

	private JoddDbDefaults defaults = new JoddDbDefaults();
	private ConnectionProvider connectionProvider = new CoreConnectionPool();
	private DbSessionProvider sessionProvider = new ThreadDbSessionProvider();
	private QueryMap queryMap = new EmptyQueryMap();
	private JoinHintResolver hintResolver = new JoinHintResolver();
	private DbEntityManager dbEntityManager = new DbEntityManager();

	/**
	 * Returns default module configuration.
	 */
	public JoddDbDefaults defaults() {
		return defaults;
	}

	public JoddDb defaults(JoddDbDefaults joddDbDefaults) {
		Objects.requireNonNull(joddDbDefaults);
		this.defaults = new JoddDbDefaults();
		return this;
	}

	/**
	 * Returns hints resolver.
	 */
	public JoinHintResolver hintResolver() {
		return hintResolver;
	}

	/**
	 * Specifies the hint resolver.
	 */
	public JoddDb hintResolver(JoinHintResolver hintResolver) {
		Objects.requireNonNull(hintResolver);
		this.hintResolver = hintResolver;
		return this;
	}

	/**
	 * Returns the manager for db entities.
	 */
	public DbEntityManager dbEntityManager() {
		return dbEntityManager;
	}

	public JoddDb dbEntityManager(DbEntityManager dbEntityManager) {
		Objects.requireNonNull(dbEntityManager);
		this.dbEntityManager = dbEntityManager;
		return this;
	}

	/**
	 * Returns the connection provider.
	 */
	public ConnectionProvider connectionProvider() {
		return connectionProvider;
	}

	/**
	 * Sets the connection provider.
	 */
	public JoddDb connectionProvider(ConnectionProvider connectionProvider) {
		Objects.requireNonNull(connectionProvider);
		this.connectionProvider = connectionProvider;
		return this;
	}

	public DbSessionProvider sessionProvider() {
		return sessionProvider;
	}

	/**
	 * Sets default session provider.
	 */
	public JoddDb sessionProvider(DbSessionProvider sessionProvider) {
		Objects.requireNonNull(sessionProvider);
		this.sessionProvider = sessionProvider;
		return this;
	}

	/**
	 * Returns {@link jodd.db.querymap.QueryMap} instance. May be <code>null</code>.
	 */
	public QueryMap queryMap() {
		return queryMap;
	}

	public JoddDb queryMap(QueryMap queryMap) {
		Objects.requireNonNull(queryMap);
		this.queryMap = queryMap;
		return this;
	}

}