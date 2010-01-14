// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper {

	/**
	 * Invoked on each row of result set. Rows numbers are 1-based.
	 */
	Object mapRow(ResultSet rs, int rowNum) throws SQLException; 

}
 