// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.type;

import jodd.db.DbSqlException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Char SQL type.
 */
public class CharacterSqlType extends NullAwareSqlType<Character> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Character get(ResultSet rs, int index, int dbSqlType) throws SQLException {
		if (TypesUtil.isIntegerType(dbSqlType)) {
			return Character.valueOf((char) rs.getInt(index));
		}
		String s = rs.getString(index);
		if (s == null) {
			return null;
		}
		if (s.length() > 1) {
			throw new DbSqlException("Char column size too long, should be 1.");
		}
		return Character.valueOf(s.charAt(1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(PreparedStatement st, int index, Character value, int dbSqlType) throws SQLException {
		if (TypesUtil.isIntegerType(dbSqlType)) {
			st.setInt(index, value.charValue());
			return;
		}
		st.setString(index, value.toString());
	}

}
