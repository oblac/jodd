// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.db.oom;

import jodd.db.oom.mapper.ResultSetMapper;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Internal result set iterator.
 */
class DbListIterator<T> implements Iterator<T> {

	protected final DbOomQuery query;
	protected final boolean entityAwareMode;
	protected final ResultSetMapper resultSetMapper;
	protected final Class[] types;
	protected final boolean closeOnEnd;

	// ---------------------------------------------------------------- ctors

	DbListIterator(final DbOomQuery query, final Class[] types, final boolean closeOnEnd) {
		this.query = query;
		this.resultSetMapper = query.executeAndBuildResultSetMapper();
		this.entityAwareMode = query.entityAwareMode;
		this.types = (types == null ? resultSetMapper.resolveTables() : types);
		this.closeOnEnd = closeOnEnd;
	}

	DbListIterator(final DbOomQuery query, final Class[] types, final ResultSetMapper resultSetMapper, final boolean closeOnEnd) {
		this.query = query;
		this.resultSetMapper = resultSetMapper;
		this.entityAwareMode = query.entityAwareMode;
		this.types = (types == null ? resultSetMapper.resolveTables() : types);
		this.closeOnEnd = closeOnEnd;
	}

	// ---------------------------------------------------------------- iterate

	protected T previousElement;
	protected T newElement;
	protected int count;
	protected boolean last;
	protected Boolean hasNext;

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns <code>true</code> if there is {@link #next() next} parsed object
	 * available.
	 */
	@Override
	public boolean hasNext() {
		if (hasNext == null) {
			hasNext = Boolean.valueOf(moveToNext());
		}
		return hasNext.booleanValue();
	}

	/**
	 * Returns next mapped object.
	 */
	@Override
	public T next() {
		if (hasNext == null) {
			hasNext = Boolean.valueOf(moveToNext());
		}
		if (hasNext == false) {
			throw new NoSuchElementException();
		}

		if (!entityAwareMode) {
			hasNext = null;
			return newElement;
		}

		count++;

		T result = previousElement;

		previousElement = newElement;

		hasNext = null;
		return result;
	}


	/**
	 * Moves to next element.
	 */
	private boolean moveToNext() {
		if (last) {
			// last has been set to true, so no more rows to iterate - close everything

			if (closeOnEnd) {
				query.close();
			} else {
				query.closeResultSet(resultSetMapper.getResultSet());
			}

			return false;
		}

		while (true) {

			if (!resultSetMapper.next()) {
				// no more rows, no more parsing, previousElement is the last one to iterate
				last = true;
				return entityAwareMode;
			}

			// parse row

			Object[] objects = resultSetMapper.parseObjects(types);

			Object row = query.resolveRowResults(objects);

			newElement = (T) row;

			if (entityAwareMode) {

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

}
