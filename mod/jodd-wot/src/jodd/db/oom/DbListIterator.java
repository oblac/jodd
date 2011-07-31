// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.mapper.ResultSetMapper;

import java.util.Iterator;

/**
 * Internal database iterator.
 * @see jodd.db.oom.DbListOneIterator
 */
class DbListIterator<T> implements Iterator<T> {

	protected DbOomQuery query;
	protected ResultSetMapper resultSetMapper;
	protected boolean closeOnEnd;
	protected Class[] types;
	protected boolean one;

	// ---------------------------------------------------------------- ctors


	DbListIterator(DbOomQuery query, Class[] types) {
		this(query, types, true);
	}
	DbListIterator(DbOomQuery query, Class[] types, boolean closeOnEnd) {
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
		return (T) query.resolveRowHints(resultSetMapper.parseObjects(types));
	}

}
