// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.log.Log;

/**
 * Returns {@link DbSession} assigned to current thread.
 * If session is not assigned to current thread, an exception is thrown, or,
 * optionally, new {@link jodd.db.DbThreadSession thread session} is created and returned.
 * <p>
 * If thread db session is created by provider, once when not needed, session has to be closed
 * explicitly. Session may be get by {@link ThreadDbSessionHolder}.
 * @see jodd.db.ThreadDbSessionHolder
 */
public class ThreadDbSessionProvider implements DbSessionProvider {

	private static final Log log = Log.getLogger(ThreadDbSessionProvider.class);

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
		if (log.isDebugEnabled()) {
			log.debug("Requesting thread session");
		}
		DbSession session = ThreadDbSessionHolder.get();
		if (session == null) {
			if (createIfMissing) {
				return new DbThreadSession();
			}
			throw new DbSqlException("No session associated to current thread.");
		}
		return session;
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeDbSession() {
		closeThreadDbSession();
	}


	/**
	 * Closes db session.
	 */
	public static void closeThreadDbSession() {
		if (log.isDebugEnabled()) {
			log.debug("Closing thread session");
		}
		DbSession session = ThreadDbSessionHolder.get();
		if (session != null) {
			session.closeSession();
		}
	}
}
