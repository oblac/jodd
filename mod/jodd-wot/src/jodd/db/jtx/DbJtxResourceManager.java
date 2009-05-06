// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.jtx;

import jodd.jtx.JtxResourceManager;
import jodd.jtx.JtxTransactionMode;
import jodd.jtx.JtxException;
import jodd.db.connection.ConnectionProvider;
import jodd.db.DbSession;
import jodd.db.DbTransactionMode;

/**
 * Database {@link jodd.jtx.JtxResourceManager} manages life-cycle of {@link jodd.db.DbSession} resources.
 * Also acts as an adapter of resource object (of any type) and JTX engine.
 * <p>
 * Transaction resources may be of any type. The only thing what is important is that resource <b>must</b> be
 * aware of its transactional state - is it in no-transactional mode (i.e. auto-commit), or under the transaction.
 */
public class DbJtxResourceManager implements JtxResourceManager<DbSession> {

	protected final ConnectionProvider connectionProvider;

	/**
	 * Creates resource manager.
	 */
	public DbJtxResourceManager(ConnectionProvider connectionProvider) {
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
	public DbSession beginTransaction(JtxTransactionMode txMode) {
		DbSession session = new DbSession(connectionProvider);
		if (txMode.isTransactional()) { 
			session.beginTransaction(new DbTransactionMode(txMode));
		}
		return session;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commitTransaction(DbSession resource) {
		if (resource.isTransactionActive()) {
			resource.commitTransaction();
		}
		resource.closeSession();
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollbackTransaction(DbSession resource) {
		try {
			if (resource.isTransactionActive()) {
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
