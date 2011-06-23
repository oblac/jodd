// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.db;

import jodd.jtx.JtxResourceManager;
import jodd.jtx.JtxTransactionMode;
import jodd.jtx.JtxException;
import jodd.db.connection.ConnectionProvider;
import jodd.db.DbSession;
import jodd.log.Log;

/**
 * Database {@link jodd.jtx.JtxResourceManager} manages life-cycle of {@link jodd.db.DbSession} resources.
 * Also acts as an adapter of resource object (of any type) and JTX engine.
 * <p>
 * Transaction resources may be of any type. The only thing what is important is that resource <b>must</b> be
 * aware of its transactional state - is it in no-transactional mode (i.e. auto-commit), or under the transaction.
 */
public class DbJtxResourceManager implements JtxResourceManager<DbSession> {

	private static final Log log = Log.getLogger(DbJtxResourceManager.class);

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
	public DbSession beginTransaction(JtxTransactionMode jtxMode, boolean active) {
		DbSession session = new DbSession(connectionProvider);
		if (active) {
			if (log.isDebugEnabled()) {
				log.debug("begin jtx");
			}
			session.beginTransaction(JtxDbUtil.convertToDbMode(jtxMode));
		}
		return session;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commitTransaction(DbSession resource) {
		if (resource.isTransactionActive()) {
			if (log.isDebugEnabled()) {
				log.debug("commit jtx");
			}
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
				if (log.isDebugEnabled()) {
					log.debug("rollback tx");
				}
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
