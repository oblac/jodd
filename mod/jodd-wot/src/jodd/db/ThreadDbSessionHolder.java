// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

/**
 * {@link DbSession} thread storage. 
 */
public class ThreadDbSessionHolder {

	private static final ThreadLocal<DbSession> DB_SESSION = new ThreadLocal<DbSession>();

	/**
	 * Returns current thread db session.
	 */
	public static DbSession get() {
		return DB_SESSION.get();
	}

	/**
	 * Sets current db session.
	 */
	public static void set(DbSession dbSession) {
		DB_SESSION.set(dbSession);
	}

	/**
	 * Removes current db session from the thread and returns it.
	 */
	public static void remove() {
		DB_SESSION.set(null);
	}
}
