// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.mapper;

import jodd.db.oom.DbOomException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Common {@link ResultSetMapper} implementation.
 */
public abstract class BaseResultSetMapper implements ResultSetMapper {

	protected final ResultSet resultSet;

	protected BaseResultSetMapper(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean next() {
		try {
			return resultSet.next();
		} catch (SQLException sex) {
			throw new DbOomException(sex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		try {
			resultSet.close();
		} catch (SQLException sex) {
			// ignore
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultSet getResultSet() {
		return resultSet;
	}


	/**
	 * {@inheritDoc}
	 */
	public Object parseOneObject(Class... types) {
		return parseObjects(types)[0];
	}

}