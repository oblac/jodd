// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.db;

import jodd.db.DbSessionProvider;
import jodd.db.DbSession;
import jodd.db.DbSqlException;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;
import jodd.log.Log;

/**
 * Returns session from the db transaction manager.
 * This session provider is made for {@link DbJtxTransactionManager}.
 */
public class DbJtxSessionProvider implements DbSessionProvider {

	private static final Log log = Log.getLogger(DbJtxSessionProvider.class);

	protected final JtxTransactionManager jtxTxManager;
	protected final JtxTransactionMode defaultTxMode;

	public DbJtxSessionProvider(JtxTransactionManager txManager) {
		this(txManager, null);
	}

	public DbJtxSessionProvider(JtxTransactionManager txManager, JtxTransactionMode defaultTxMode) {
		this.jtxTxManager = txManager;
		this.defaultTxMode = defaultTxMode;
	}

	/**
	 * {@inheritDoc}
	 */
	public DbSession getDbSession() {
		if (log.isDebugEnabled()) {
			log.debug("Requesting db TX manager session");
		}
		DbJtxTransaction jtx = (DbJtxTransaction) jtxTxManager.getTransaction();
		if (jtx == null) {
			if (defaultTxMode != null) {
				jtx = (DbJtxTransaction) jtxTxManager.requestTransaction(defaultTxMode, null);
				return jtx.requestResource();
			}
			throw new DbSqlException("No transaction is in progress and DbSession can't be provided. It seems that transaction manager is not used to begin a transaction.");
		}
		return jtx.requestResource();
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeDbSession() {
		if (log.isDebugEnabled()) {
			log.debug("Closing db TX manager session");
		}
		DbJtxTransaction jtx = (DbJtxTransaction) jtxTxManager.getTransaction();
		if (jtx != null) {
			jtx.commit();
		}
	}
}
