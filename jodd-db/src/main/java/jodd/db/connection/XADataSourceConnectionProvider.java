// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.connection;

import jodd.db.DbSqlException;

import javax.sql.XADataSource;
import javax.sql.XAConnection;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DataSource connection provider.
 */
public class XADataSourceConnectionProvider implements ConnectionProvider {

	private XADataSource xaDataSource;
	private String username;
	private String password;

	public XADataSourceConnectionProvider(XADataSource dataSource) {
		this.xaDataSource = dataSource;
		this.username = this.password = null;
	}
	public XADataSourceConnectionProvider(XADataSource dataSource, String user, String pass) {
		this.xaDataSource = dataSource;
		this.username = user;
		this.password = pass;
	}

	public void init() {}

	public Connection getConnection() {
		try {
			XAConnection xaConnection;
			if (username != null) {
				xaConnection = xaDataSource.getXAConnection(username, password);
			} else {
				xaConnection = xaDataSource.getXAConnection();
			}
			return xaConnection.getConnection();
		} catch (SQLException sex) {
			throw new DbSqlException("Unable to get connection from XA datasource.", sex);
		}
	}

	public void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException sex) {
			// ignore
		}
	}

	public void close() {}
}
