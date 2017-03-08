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

package jodd.db.jtx;

import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransaction;
import jodd.jtx.JtxTransactionMode;
import jodd.db.connection.ConnectionProvider;

/**
 * {@link jodd.jtx.JtxTransactionManager} that uses only <b>one</b> JTX db resource type.
 * Usually, applications have only one transactional resource type - the database.
 * This manager just simplifies the usage, nothing more.
 * @see jodd.jtx.JtxTransactionManager
 */
public class DbJtxTransactionManager extends JtxTransactionManager {

	/**
	 * Creates db jtx manager and registers provided {@link DbJtxResourceManager}.
	 */
	public DbJtxTransactionManager(DbJtxResourceManager resourceManager) {
		setMaxResourcesPerTransaction(1);
		setSingleResourceManager(true);
		super.registerResourceManager(resourceManager);

	}

	/**
	 * Creates db jtx manager and registers new {@link DbJtxResourceManager}.
	 */
	public DbJtxTransactionManager(ConnectionProvider connectionProvider) {
		this(new DbJtxResourceManager(connectionProvider));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DbJtxTransaction requestTransaction(JtxTransactionMode mode) {
		return (DbJtxTransaction) super.requestTransaction(mode, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DbJtxTransaction requestTransaction(JtxTransactionMode mode, Object scope) {
		return (DbJtxTransaction) super.requestTransaction(mode, scope);
	}

	/**
	 * Builds new transaction instance.
	 */
	@Override
	protected JtxTransaction createNewTransaction(JtxTransactionMode tm, Object scope, boolean active) {
		return new DbJtxTransaction(this, tm, scope, active);
	}

}