// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

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
			throw new DbSqlException("Named parameter '" + name + "' not found.");
		}
		return positions;
	}

	Iterator<String> iterateNamedParameters() {
		return namedParameterLocationMap.keySet().iterator();
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
				if (right < 0) {
					right = stringLength;
				}
				String param = sqlString.substring(index + 1, right);
				paramCount++;
				storeNamedParameter(param, paramCount);
				pureSql.append('?');
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
						throw new DbSqlException("Positional parameter '" + nfex + "' is not an integral ordinal.", nfex);
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
