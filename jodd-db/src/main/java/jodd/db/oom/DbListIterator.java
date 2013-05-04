// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom;

import jodd.db.oom.mapper.ResultSetMapper;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Internal result set iterator.
 */
class DbListIterator<T> implements Iterator<T> {

	protected DbOomQuery query;
	protected ResultSetMapper resultSetMapper;
	protected boolean closeOnEnd;
	protected Class[] types;
	protected boolean one;

	// ---------------------------------------------------------------- ctors

	DbListIterator(DbOomQuery query, Class[] types, boolean closeOnEnd) {
		this.query = query;
		this.resultSetMapper = query.executeAndBuildResultSetMapper();
		this.types = (types == null ? resultSetMapper.resolveTables() : types);
		this.closeOnEnd = closeOnEnd;
	}

	// ---------------------------------------------------------------- iterate

	protected T previousElement;
	protected T newElement;
	protected int count;
	protected boolean last;

	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns <code>true</code> if there is {@link #next() next} parsed object
	 * available.
	 */
	public boolean hasNext() {
		if (last) {
			// last has been set to true, so no more rows to iterate - close everything

			if (closeOnEnd == true) {
				query.close();
			} else {
				query.closeResultSet(resultSetMapper.getResultSet());
			}

			return false;
		}

		while (true) {

			if (resultSetMapper.next() == false) {
				// no more rows, no more parsing, previousElement is the last one to iterate
				last = true;
				return true;
			}

			// parse row

			Object[] objects = resultSetMapper.parseObjects(types);

			Object row = query.resolveRowHints(objects);

			newElement = (T) row;

			if (query.entityAwareMode) {

				if (count == 0 && previousElement == null) {
					previousElement = newElement;

					continue;
				}

				if (previousElement != null && newElement != null) {
					boolean equals;

					if (newElement.getClass().isArray()) {
						equals = Arrays.equals((Object[]) previousElement, (Object[]) newElement);
					} else {
						equals = previousElement.equals(newElement);
					}

					if (equals) {
						continue;
					}
				}
			}

			break;
		}

		return true;
	}

	/**
	 * Returns next mapped object.
	 */
	public T next() {
		count++;

		T result = previousElement;

		previousElement = newElement;

		return result;
	}

}
