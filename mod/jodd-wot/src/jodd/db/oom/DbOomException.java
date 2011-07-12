// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbSqlException;

/**
 * DbOom exceptions.
 */
public class DbOomException extends DbSqlException {

	public DbOomException(Throwable t) {
		super(t);
	}

	public DbOomException() {
	}

	public DbOomException(String message) {
		super(message);
	}

	public DbOomException(String message, Throwable t) {
		super(message, t);
	}
}
