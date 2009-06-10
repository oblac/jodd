// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

/**
 * Returns {@link DbSession} assigned to current thread.
 * If session is not assigned to current thread, an exception is thrown, or,
 * optionally, new {@link jodd.db.DbThreadSession thread session} is created and returned. 
 * @see jodd.db.ThreadDbSessionHolder
 */
public class ThreadDbSessionProvider implements DbSessionProvider {

	protected final boolean createIfMissing;

	public ThreadDbSessionProvider() {
		this(false);
	}

	public ThreadDbSessionProvider(boolean createIfMissing) {
		this.createIfMissing = createIfMissing;
	}

	/**
	 * {@inheritDoc}
	 */
	public DbSession getDbSession() {
		DbSession session = ThreadDbSessionHolder.get();
		if (session == null) {
			if (createIfMissing) {
				return new DbThreadSession();
			}
			throw new DbSqlException("No session associated to current thread.");
		}
		return session;
	}
}
