// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

/**
 * Provide {@link DbSession} when requested by {@link DbQuery} inside of some context.
 */
public interface DbSessionProvider {

	/**
	 * Returns {@link jodd.db.DbSession} instance. May throws an exception if session can not be provided.
	 */
	DbSession getDbSession();

	/**
	 * Closes db session if opened in some context.
	 */
	void closeDbSession();
}
