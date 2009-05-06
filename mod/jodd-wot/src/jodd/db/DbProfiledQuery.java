// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Profiled {@link DbQuery} measures query execution time. May be used for debugging purposes. 
 */
public class DbProfiledQuery extends DbQuery {

	public DbProfiledQuery(Connection conn, String sql) {
		super(conn, sql);
	}

	public DbProfiledQuery(DbSession session, String sqlString) {
		super(session, sqlString);
	}

	public DbProfiledQuery(String sqlString) {
		super(sqlString);
	}

	// ---------------------------------------------------------------- profile

	long start;
	long elapsed = -1;

	@Override
	public int executeUpdate() {
		start = System.currentTimeMillis();
		int result = super.executeUpdate();
		elapsed = System.currentTimeMillis() - start;
		return result;
	}

	@Override
	public ResultSet execute() {
		start = System.currentTimeMillis();
		ResultSet result = super.execute();
		elapsed = System.currentTimeMillis() - start;
		return result;
	}

	@Override
	public long executeCount() {
		start = System.currentTimeMillis();
		long count = super.executeCount();
		elapsed = System.currentTimeMillis() - start;
		return count;
	}

	/**
	 * Returns query execution elapsed time in ms.
	 * Returns <code>-1</code> if query is still not executed.
	 */
	public long getExecutionElapsedTime() {
		return elapsed;
	}

	// ---------------------------------------------------------------- toString

	@Override
	public String getQueryString() {
		StringBuilder result = new StringBuilder(super.getQueryString());
		if (elapsed != -1) {
			result.append("\nExecution time: ").append(elapsed).append("ms.");
		}
		return result.toString();
	}

}
