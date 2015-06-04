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

package jodd.db;

import jodd.util.CharUtil;
import jodd.util.StringUtil;
import jodd.util.collection.IntArrayList;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * SQL parameters parser that recognizes named and ordinal parameters.
 */
class DbQueryParser {

	public static final String SQL_SEPARATORS = " \n\r\f\t,()=<>&|+-=/*'^![]#~\\";

	boolean prepared;
	String sql;

	// ---------------------------------------------------------------- ctors

	DbQueryParser() {
	}

	DbQueryParser(String sql) {
		parseSql(sql);
	}

	// ---------------------------------------------------------------- parameters

	private Map<String, IntArrayList> namedParameterLocationMap;

	private void storeNamedParameter(String name, int position) {
		IntArrayList locations = namedParameterLocationMap.get(name);
		if (locations == null) {
			locations = new IntArrayList();
			namedParameterLocationMap.put(name, locations);
		}
		locations.add(position);
	}

	IntArrayList lookupNamedParameterIndices(String name) {
		return namedParameterLocationMap.get(name);
	}

	IntArrayList getNamedParameterIndices(String name) {
		IntArrayList positions = namedParameterLocationMap.get(name);
		if (positions == null) {
			throw new DbSqlException("Named parameter not found: " + name + "\nQuery: " + sql);
		}
		return positions;
	}

	Iterator<String> iterateNamedParameters() {
		return namedParameterLocationMap.keySet().iterator();
	}

	// ---------------------------------------------------------------- batch

	private Map<String, Integer> batchParams;

	private void saveBatchParameter(String name, int size) {
		if (batchParams == null) {
			batchParams = new HashMap<String, Integer>();
		}
		batchParams.put(name, Integer.valueOf(size));
	}

	/**
	 * Returns the size of batch parameter. Returns <code>0</code>
	 * if parameter does not exist.
	 */
	protected int getBatchParameterSize(String name) {
		if (batchParams == null) {
			return 0;
		}
		Integer size = batchParams.get(name);
		if (size == null) {
			return 0;
		}
		return size.intValue();
	}

	// ---------------------------------------------------------------- parser

	void parseSql(String sqlString) {
		namedParameterLocationMap = new HashMap<String, IntArrayList>();
		int stringLength = sqlString.length();
		StringBuilder pureSql = new StringBuilder(stringLength);
		boolean inQuote = false;
		int index = 0;
		int paramCount = 0;
		while (index < stringLength) {
			char c = sqlString.charAt(index);
			if (inQuote == true) {
				if (c == '\'') {
					inQuote = false;
				}
			} else if (c == '\'') {
				inQuote = true;
			} else if (c == ':') {
				int right = StringUtil.indexOfChars(sqlString, SQL_SEPARATORS, index + 1);
				boolean batch = false;

				if (right < 0) {
					right = stringLength;
				} else {
					if (sqlString.charAt(right) == '!') {
						batch = true;
					}
				}

				String param = sqlString.substring(index + 1, right);

				if (!batch) {
					paramCount++;
					storeNamedParameter(param, paramCount);
					pureSql.append('?');
				}
				else {
					// read batch size
					right++;
					int numStart = right;

					while (right < stringLength) {
						if (!CharUtil.isDigit(sqlString.charAt(right))) {
							break;
						}
						right++;
					}

					String numberValue = sqlString.substring(numStart, right);
					int batchSize;
					try {
						batchSize = Integer.parseInt(numberValue);
					} catch (NumberFormatException nfex) {
						throw new DbSqlException("Batch size is not an integer: " + numberValue, nfex);
					}

					saveBatchParameter(param, batchSize);

					// create batch parameters
					for (int i = 1; i <= batchSize; i++) {
						if (i != 1) {
							pureSql.append(',');
						}
						paramCount++;
						storeNamedParameter(param + '.' + i, paramCount);
						pureSql.append('?');
					}
				}

				index = right;
				continue;
			} else if (c == '?') {		// either an ordinal or positional parameter
				if ((index < stringLength - 1) && (Character.isDigit(sqlString.charAt(index + 1)))) {   // positional parameter
					int right = StringUtil.indexOfChars(sqlString, SQL_SEPARATORS, index + 1);
					if (right < 0) {
						right = stringLength;
					}
					String param = sqlString.substring(index + 1, right);
					try {
						Integer.parseInt(param);
					} catch (NumberFormatException nfex) {
						throw new DbSqlException("Positional parameter is not an integer: " + param, nfex);
					}
					paramCount++;
					storeNamedParameter(param, paramCount);
					pureSql.append('?');
					index = right;
					continue;
				}
				paramCount++;		// ordinal param
			}
			pureSql.append(c);
			index++;
		}
		this.prepared = (paramCount != 0);
		this.sql = pureSql.toString();
	}
}
