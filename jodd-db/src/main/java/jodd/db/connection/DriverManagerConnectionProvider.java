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
			throw new DbSqlException("JDBC driver not found: " + driverClass, cnfex);
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
			throw new DbSqlException("Connection not found", sex);
		}
		return conn;
	}

	public void closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException sex) {
			// ignore
		}
	}

	public void close() {
	}
}