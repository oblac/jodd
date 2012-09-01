// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.debug;

import jodd.db.DbHsqldbTestCase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LoggablePreparedStatementTest extends DbHsqldbTestCase {

	public void testSetParameters() throws SQLException {
		Connection connection = cp.getConnection();

		PreparedStatement preparedStatement = LoggablePreparedStatementFactory.create(connection, "select * from BOY t where t.id=?");
		preparedStatement.setInt(1, 7);
		preparedStatement.close();

		assertEquals("select * from BOY t where t.id=7", LoggablePreparedStatementFactory.getQueryString(preparedStatement));

		preparedStatement = LoggablePreparedStatementFactory.create(connection, "select * from BOY t where t.name=?");
		preparedStatement.setString(1, "jodd");
		preparedStatement.close();

		assertEquals("select * from BOY t where t.name='jodd'", LoggablePreparedStatementFactory.getQueryString(preparedStatement));

		preparedStatement = LoggablePreparedStatementFactory.create(connection, "select * from BOY t where t.name=?");
		preparedStatement.setNull(1, Types.VARCHAR, "String");
		preparedStatement.close();

		assertEquals("select * from BOY t where t.name=<null>", LoggablePreparedStatementFactory.getQueryString(preparedStatement));

		preparedStatement = LoggablePreparedStatementFactory.create(connection, "select * from BOY t where t.id=?");
		preparedStatement.setFloat(1, 1.73f);
		preparedStatement.close();

		assertEquals("select * from BOY t where t.id=1.73", LoggablePreparedStatementFactory.getQueryString(preparedStatement));

		preparedStatement = LoggablePreparedStatementFactory.create(connection, "select * from BOY t where t.id=? and t.name=?");
		preparedStatement.setString(2, "jodd");
		preparedStatement.setDouble(1, 1.73);
		preparedStatement.close();

		assertEquals("select * from BOY t where t.id=1.73 and t.name='jodd'", LoggablePreparedStatementFactory.getQueryString(preparedStatement));

		cp.closeConnection(connection);
	}

}
