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

import jodd.db.DbSession;
import jodd.db.DbSessionProvider;
import jodd.db.DbSqlException;
import jodd.jtx.JtxTransactionManager;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

/**
 * Returns session from the db {@link JtxTransactionManager transaction manager},
 * like {@link DbJtxTransactionManager}.
 */
public class DbJtxSessionProvider implements DbSessionProvider {

	private static final Logger log = LoggerFactory.getLogger(DbJtxSessionProvider.class);

	protected final JtxTransactionManager jtxTxManager;

	/**
	 * Creates new JTX session provider.
	 */
	public DbJtxSessionProvider(final JtxTransactionManager txManager) {
		this.jtxTxManager = txManager;
	}

	/**
	 * Returns session from JTX transaction manager and started transaction.
	 */
	@Override
	public DbSession getDbSession() {
		log.debug("Requesting db TX manager session");

		final DbJtxTransaction jtx = (DbJtxTransaction) jtxTxManager.getTransaction();

		if (jtx == null) {
			throw new DbSqlException(
					"No transaction is in progress and DbSession can't be provided. " +
					"It seems that transaction manager is not used to begin a transaction.");
		}

		return jtx.requestResource();
	}

}
