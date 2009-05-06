// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.jtx;

import jodd.db.DbSessionProvider;
import jodd.db.DbSession;
import jodd.db.DbSqlException;
import jodd.jtx.JtxTransactionManager;

/**
 * Returns session from the transaction manager.
 */
public class DbJtxSessionProvider implements DbSessionProvider {

	protected final JtxTransactionManager jtxTxManager;

	public DbJtxSessionProvider(JtxTransactionManager txManager) {
		this.jtxTxManager = txManager;
	}

	/**
	 * {@inheritDoc}
	 */
	public DbSession getDbSession() {
		DbJtxTransaction jtx = (DbJtxTransaction) jtxTxManager.getTransaction();
		if (jtx == null) {
			throw new DbSqlException("No transaction is assigned to this thread and DbSession can't be provided. It seems that transaction manager is not used to begin a transaction.");
		}
		return jtx.requestResource();
	}
}
