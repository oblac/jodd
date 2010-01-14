// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm;

import jodd.db.orm.mapper.ResultSetMapper;

import java.util.Iterator;

/**
 * Internal database iterator.
 * @see jodd.db.orm.DbListOneIterator
 */
class DbListIterator<T> implements Iterator<T> {

	protected DbOrmQuery query;
	protected ResultSetMapper resultSetMapper;
	protected boolean closeOnEnd;
	protected Class[] types;
	protected boolean one;

	// ---------------------------------------------------------------- ctors


	DbListIterator(DbOrmQuery query, Class[] types) {
		this(query, types, true);
	}
	DbListIterator(DbOrmQuery query, Class[] types, boolean closeOnEnd) {
		this.query = query;
		this.resultSetMapper = query.executeAndBuildResultSetMapper();
		this.types = (types == null ? resultSetMapper.resolveTables() : types);
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
		return (T) query.prepareRow(resultSetMapper.parseObjects(types));
	}

}
