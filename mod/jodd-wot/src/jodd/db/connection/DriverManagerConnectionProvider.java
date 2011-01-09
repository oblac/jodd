// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.connection;

import jodd.db.DbSqlException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Driver manager connection provider;
 */
public class DriverManagerConnectionProvider implements ConnectionProvider {

	// ---------------------------------------------------------------- properties

	private String url;
	private String username;
	private String password;
	private String driverClass;
	private Integer isolation;
	private Boolean autoCommit;


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public Integer getIsolation() {
		return isolation;
	}

	public void setIsolation(Integer isolation) {
		this.isolation = isolation;
	}

	public Boolean getAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(Boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	// ---------------------------------------------------------------- ctors

	public DriverManagerConnectionProvider(String driverClass, String url, String username, String password) {
		this.driverClass = driverClass;
		this.password = password;
		this.username = username;
		this.url = url;
	}
	public DriverManagerConnectionProvider(String driverClass, String url) {
		this.driverClass = driverClass;
		this.url = url;
	}

	public DriverManagerConnectionProvider() {
		
	}

	// ---------------------------------------------------------------- provider


	public void init() {
		try {
			Class.forName(driverClass);
		} catch (ClassNotFoundException cnfex) {
			throw new DbSqlException("Unable to find JDBC driver class: '" + driverClass + '\'', cnfex);
		}
	}

	public Connection getConnection() {
		Connection conn;
		try {
			if (username != null) {
				conn = DriverManager.getConnection(url, username, password);
			} else {
				conn = DriverManager.getConnection(url);
			}
			if (isolation != null) {
				conn.setTransactionIsolation(isolation.intValue());
			}
			if (autoCommit != null) {
				conn.setAutoCommit(autoCommit.booleanValue());
			}
		}
		catch (SQLException sex) {
			throw new DbSqlException("Unable to get connection.", sex);
		}
		return conn;
	}

	public void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException sex) {
			// ignore
		}
	}

	public void close() {
	}
}