// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.jtx;

import jodd.db.DbSessionProvider;
import jodd.db.DbSession;
import jodd.db.DbSqlException;
import jodd.jtx.JtxTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public DbJtxSessionProvider(JtxTransactionManager txManager) {
		this.jtxTxManager = txManager;
	}

	/**
	 * Returns session from JTX transaction manager and started transaction.
	 */
	public DbSession getDbSession() {
		log.debug("Requesting db TX manager session");

		DbJtxTransaction jtx = (DbJtxTransaction) jtxTxManager.getTransaction();

		if (jtx == null) {
			throw new DbSqlException(
					"No transaction is in progress and DbSession can't be provided. " +
					"It seems that transaction manager is not used to begin a transaction.");
		}

		return jtx.requestResource();
	}

}
