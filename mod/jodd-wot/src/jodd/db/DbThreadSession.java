// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.db.connection.ConnectionProvider;

/**
 * Thread assigned {@link jodd.db.DbSession}. Upon creation, it assigns
 * the session to current thread. Useful when only one session (i.e. connection)
 * is used per thread, through service layers.
 * <p>
 * {@link jodd.db.DbThreadSession} uses {@link jodd.db.ThreadDbSessionHolder} for storing
 * created sessions in the thread storage. Note that holder may be manipulated from outside
 * of this class.
 */
public class DbThreadSession extends DbSession {

	/**
	 * Creates new db session and assigns it to the current thread.
	 * Closes already assigned session, if any exist. 
	 * @param connectionProvider connection provider
	 */
	public DbThreadSession(ConnectionProvider connectionProvider) {
		super(connectionProvider);
		DbSession session = ThreadDbSessionHolder.get();
		if (session != null) {
			session.closeSession();
		}
		ThreadDbSessionHolder.set(this);
	}

	/**
	 * Creates new db session and assigns it to the current thread, using
	 * default connection provider.
	 */
	public DbThreadSession() {
		this(DbDefault.connectionProvider);
	}

	
	/**
	 * Closes current session and remove the association from current thread.
	 * @see jodd.db.DbSession#closeSession()
	 */
	@Override
	public void closeSession() {
		ThreadDbSessionHolder.remove();
		super.closeSession();
	}

	// ---------------------------------------------------------------- static stuff

	/**
	 * Returns current thread session or <code>null</code> if no session is assigned
	 * to a thread.
	 */
	public static DbSession getCurrentSession() {
		return ThreadDbSessionHolder.get();
	}

	/**
	 * Returns existing thread session, or new one if already not exist. If session doesn't exist, it will be created
	 * using default connection provider.
	 */
	public static DbThreadSession getThreadSession() {
		DbThreadSession session = (DbThreadSession) ThreadDbSessionHolder.get();
		if (session == null) {
			session = new DbThreadSession();
		}
		return session;
	}

	/**
	 * Closes thread session.
	 */
	public static void closeThreadSession() {
		DbThreadSession session = (DbThreadSession) ThreadDbSessionHolder.get();
		if (session != null) {
			session.closeSession();
		}
	}

}
