// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jtx.db;

import jodd.db.DbTransactionMode;
import jodd.jtx.JtxTransactionMode;

/**
 * Helpers for jtx db.
 */
public class JtxDbUtil {

	/**
	 * Convert JTX transaction mode to DB transaction mode.
	 */
	public static DbTransactionMode convertToDbMode(JtxTransactionMode txMode) {
		int isolation = -1;
		switch (txMode.getIsolationLevel()) {
			case ISOLATION_DEFAULT: isolation = DbTransactionMode.ISOLATION_DEFAULT; break;
			case ISOLATION_NONE: isolation = DbTransactionMode.ISOLATION_NONE; break;
			case ISOLATION_READ_COMMITTED: isolation = DbTransactionMode.ISOLATION_READ_COMMITTED; break;
			case ISOLATION_READ_UNCOMMITTED: isolation = DbTransactionMode.ISOLATION_READ_UNCOMMITTED; break;
			case ISOLATION_REPEATABLE_READ: isolation = DbTransactionMode.ISOLATION_REPEATABLE_READ; break;
			case ISOLATION_SERIALIZABLE: isolation = DbTransactionMode.ISOLATION_SERIALIZABLE; break;
		}
		DbTransactionMode result = new DbTransactionMode();
		result.setIsolation(isolation);
		result.setReadOnly(txMode.isReadOnly());
		return result;
	}
}
