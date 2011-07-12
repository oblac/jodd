// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.mapper.ResultSetMapper;

import java.util.Iterator;

/**
 * Internal database iterator for single type.
 */
class DbListOneIterator<T> implements Iterator<T> {

	protected DbOrmQuery query;
	protected ResultSetMapper resultSetMapper;
	protected boolean closeOnEnd;
	protected Class type;
	protected boolean one;

	// ---------------------------------------------------------------- ctors


	DbListOneIterator(DbOrmQuery query, Class<T> type) {
		this(query, type, true);
	}

	DbListOneIterator(DbOrmQuery query, Class<T> type, boolean closeOnEnd) {
		this.query = query;
		this.resultSetMapper = query.executeAndBuildResultSetMapper();
		this.type = (type == null ? resultSetMapper.resolveTables()[0] : type);
		this.closeOnEnd = closeOnEnd;
	}


	// ---------------------------------------------------------------- iterate

	public void remove() {
		throw new UnsupportedOperationException("Removing is not supported.");
	}

	public boolean hasNext() {
		if (resultSetMapper.next() == true) {
			return true;
		}
		if (closeOnEnd == true) {
			query.close();
		} else {
			query.closeResultSet(resultSetMapper.getResultSet());
		}
		return false;
	}

	@SuppressWarnings({"unchecked"})
	public T next() {
		return (T) resultSetMapper.parseOneObject(type);
	}

}
