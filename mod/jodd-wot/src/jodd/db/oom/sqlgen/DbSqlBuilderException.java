// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.sqlgen;

import jodd.db.oom.DbOrmException;

public class DbSqlBuilderException extends DbOrmException {

	public DbSqlBuilderException(Throwable t) {
		super(t);
	}

	public DbSqlBuilderException() {
	}

	public DbSqlBuilderException(String message) {
		super(message);
	}

	public DbSqlBuilderException(String message, Throwable t) {
		super(message, t);
	}

	protected String queryString;

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	public String getMessage() {
        String message = super.getMessage();
		if (queryString != null) {
			message += " Generated query: " + queryString;
		}
		return message;
	}
}
