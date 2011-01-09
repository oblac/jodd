// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import java.sql.Types;

/**
 * Some java.sql.Types utilities.
 */
public class TypesUtil {

	/**
	 * Returns <code>true</code> if type is some integer-like type: INTEGER, SMALLINT, TINYINT, BIT.
	 */
	public static boolean isIntegerType(int type) {
		return (type == Types.INTEGER) || (type == Types.SMALLINT) || (type == Types.TINYINT) || (type == Types.BIT);
	}

	/**
	 * Returns <code>true</code> if type is some string-like type: CHAR, VARCHAR.
	 */
	public static boolean isStringType(int type) {
		return (type == Types.VARCHAR) || (type == Types.CHAR);
	}
}
