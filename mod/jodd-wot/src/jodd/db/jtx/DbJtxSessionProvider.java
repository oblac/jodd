// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.jtx;

import jodd.db.DbSessionProvider;
import jodd.db.DbSession;
import jodd.db.DbSqlException;
import jodd.jtx.JtxTransactionManager;
import jodd.jtx.JtxTransactionMode;

/**
 * Returns session from the db transaction manager.
 * This session provider is made for {@link jodd.db.jtx.DbJtxTransactionManager}.
 */
public class DbJtxSessionProvider implements DbSessionProvider {

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
		DbJtxTransaction jtx = (DbJtxTransaction) jtxTxManager.getTransaction();
		if (jtx == null) {
			if (defaultTxMode != null) {
				jtx = (DbJtxTransaction) jtxTxManager.requestTransaction(defaultTxMode);
				return jtx.requestResource();
			}
			throw new DbSqlException("No transaction is assigned to this thread and DbSession can't be provided. It seems that transaction manager is not used to begin a transaction.");
		}
		return jtx.requestResource();
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeDbSession() {
		DbJtxTransaction jtx = (DbJtxTransaction) jtxTxManager.getTransaction();
		if (jtx != null) {
			jtx.commit();
		}
	}
}
