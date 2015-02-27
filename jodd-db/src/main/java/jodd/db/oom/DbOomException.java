// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.DbQuery;
import jodd.db.DbSqlException;

/**
 * DbOom exceptions.
 */
public class DbOomException extends DbSqlException {

	public DbOomException(Throwable t) {
		super(t);
	}

	public DbOomException(String message) {
		super(message);
	}

	public DbOomException(DbQuery dbQuery, String message) {
		super(dbQuery, message);
	}

	public DbOomException(String message, Throwable t) {
		super(message, t);
	}

	public DbOomException(DbQuery dbQuery, String message, Throwable t) {
		super(dbQuery, message, t);
	}

}