// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.sqlgen;

import jodd.db.oom.DbOomException;

public class DbSqlBuilderException extends DbOomException {

	public DbSqlBuilderException(String message) {
		super(message);
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
