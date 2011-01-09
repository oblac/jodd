// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.connection.ConnectionProvider;

/**
 * Db defaults.
 */
@SuppressWarnings({"RedundantFieldInitialization"})
public final class DbDefault {

	public static boolean forcePreparedStatement = false;

	public static DbSessionProvider sessionProvider = new ThreadDbSessionProvider();

	public static int type = DbQuery.TYPE_FORWARD_ONLY;

	public static int concurrencyType = DbQuery.CONCUR_READ_ONLY;

	public static int holdability = DbQuery.DEFAULT_HOLDABILITY;

	public static boolean debug = false;

	public static int fetchSize = 0;

	public static int maxRows = 0; 

	public static DbTransactionMode transactionMode = new DbTransactionMode();

	public static ConnectionProvider connectionProvider = null;
}
