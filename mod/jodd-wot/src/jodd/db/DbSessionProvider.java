// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

/**
 * Provide {@link DbSession} when requested by {@link DbQuery}.
 */
public interface DbSessionProvider {

	/**
	 * Returns {@link jodd.db.DbSession} instance or throws an exception if session can not be provided.
	 */
	DbSession getDbSession();
}
