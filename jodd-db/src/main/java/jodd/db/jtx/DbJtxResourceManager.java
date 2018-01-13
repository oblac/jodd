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

import jodd.jtx.JtxResourceManager;
import jodd.jtx.JtxTransactionMode;
import jodd.jtx.JtxException;
import jodd.db.connection.ConnectionProvider;
import jodd.db.DbSession;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

/**
 * Database {@link jodd.jtx.JtxResourceManager} manages life-cycle of {@link jodd.db.DbSession} resources.
 * Also acts as an adapter of resource object (of any type) and JTX engine.
 * <p>
 * Transaction resources may be of any type. The only thing what is important is that resource <b>must</b> be
 * aware of its transactional state - is it in no-transactional mode (i.e. auto-commit), or under the transaction.
 */
public class DbJtxResourceManager implements JtxResourceManager<DbSession> {

	private static final Logger log = LoggerFactory.getLogger(DbJtxResourceManager.class);

	protected final ConnectionProvider connectionProvider;

	/**
	 * Creates resource manager.
	 */
	public DbJtxResourceManager(final ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<DbSession> getResourceType() {
		return DbSession.class;
	}

	// ---------------------------------------------------------------- resource manager

	/**
	 * {@inheritDoc}
	 */
	public DbSession beginTransaction(final JtxTransactionMode jtxMode, final boolean active) {
		DbSession session = new DbSession(connectionProvider);
		if (active) {
			log.debug("begin jtx");

			session.beginTransaction(JtxDbUtil.convertToDbMode(jtxMode));
		}
		return session;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commitTransaction(final DbSession resource) {
		if (resource.isTransactionActive()) {
			log.debug("commit jtx");

			resource.commitTransaction();
		}
		resource.closeSession();
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollbackTransaction(final DbSession resource) {
		try {
			if (resource.isTransactionActive()) {
				log.debug("rollback tx");

				resource.rollbackTransaction();
			}
		} catch (Exception ex) {
			throw new JtxException(ex);
		} finally {
			resource.closeSession();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		connectionProvider.close();
	}

}
