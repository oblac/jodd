// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

/**
 * Provide {@link DbSession} when requested by {@link DbQuery}.
 * Important: <code>DbSessionProvider</code> implementations
 * should NOT create new db sessions! <code>DbSession</code> should
 * be already created and somehow assigned to <code>DbSessionProvider</code>
 * implementation. User must control session opening and closing,
 * and not <code>DbSessionProvider</code>, as we can not figure
 * weather connection should be closed after closing a query;
 * or still hold on open for the next query.
 */
public interface DbSessionProvider {

	/**
	 * Returns {@link DbSession}. May throws an exception
	 * if session can not be provided.
	 */
	DbSession getDbSession();

}
