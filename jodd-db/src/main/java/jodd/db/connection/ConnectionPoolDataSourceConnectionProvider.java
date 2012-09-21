// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.connection;

import jodd.db.DbSqlException;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection pool data source {@link jodd.db.connection.ConnectionProvider}
 */
public class ConnectionPoolDataSourceConnectionProvider implements ConnectionProvider {
	// ---------------------------------------------------------------- properties

	private ConnectionPoolDataSource cpds;

	public ConnectionPoolDataSourceConnectionProvider(ConnectionPoolDataSource cpds) {
		this.cpds = cpds;
	}

	// ---------------------------------------------------------------- init/close

	public void init() {
	}

	public void close() {
		cpds = null;
	}

	// ---------------------------------------------------------------- get/free

	public Connection getConnection() {
		PooledConnection pconn;
		try {
			pconn = cpds.getPooledConnection();
		} catch (SQLException sex) {
			throw new DbSqlException("Unable to get pooled connection.", sex);
		}
		try {
			return pconn.getConnection();
		} catch (SQLException sex) {
			throw new DbSqlException("Unable to get connection from pooled connection.", sex);
		}
	}

	public void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException sex) {
			// ignore
		}
	}
}
