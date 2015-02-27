// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import jodd.exception.UncheckedException;

/**
 * Unchecked SQL exception.
 */
public class DbSqlException extends UncheckedException {

	public DbSqlException(Throwable t) {
		super(t);
	}

	public DbSqlException(String message) {
		super(message);
	}

	public DbSqlException(DbQueryBase dbQuery, String message) {
		super(message + "\nQuery: " + dbQuery.getQueryString());
	}

	public DbSqlException(String message, Throwable t) {
		super(message, t);
	}

	public DbSqlException(DbQueryBase dbQuery, String message, Throwable t) {
		super(message + "\nQuery: " + dbQuery.getQueryString(), t);
	}

}