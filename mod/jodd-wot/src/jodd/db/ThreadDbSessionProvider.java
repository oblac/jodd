// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

/**
 * Returns {@link DbSession} assigned to current thread.
 * @see jodd.db.ThreadDbSessionHolder
 */
public class ThreadDbSessionProvider implements DbSessionProvider {

	/**
	 * {@inheritDoc}
	 */
	public DbSession getDbSession() {
		DbSession session = ThreadDbSessionHolder.get();
		if (session == null) {
			throw new DbSqlException("No session associated to current thread.");
		}
		return session;
	}
}
