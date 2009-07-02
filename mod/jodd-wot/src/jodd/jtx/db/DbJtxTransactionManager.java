// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.db;

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
	 * Creates db jtx manager and registeres new {@link DbJtxResourceManager}.
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
	public DbJtxTransaction requestTransaction(JtxTransactionMode mode, Object context) {
		return (DbJtxTransaction) super.requestTransaction(mode, context);
	}

	/**
	 * Builds new transaction instance.
	 */
	@Override
	protected JtxTransaction createNewTransaction(JtxTransactionMode tm, Object context) {
		return new DbJtxTransaction(this, tm, context);
	}

}