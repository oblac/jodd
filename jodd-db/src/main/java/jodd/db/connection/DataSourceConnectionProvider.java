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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DataSource connection provider. Note that data source implementation might
 * not support <code>dataSource.getConnection(username, password)</code>. 
 */
public class DataSourceConnectionProvider implements ConnectionProvider {

	private final DataSource dataSource;
	private final String username;
	private final String password;

	public DataSourceConnectionProvider(final String jndiName) {
		this(jndiName, null, null);
	}

	public DataSourceConnectionProvider(final String jndiName, final String user, final String pass) {
		try {
			InitialContext initialContext = new InitialContext();

			this.dataSource = (DataSource) initialContext.lookup(jndiName);
		} catch (NamingException nex) {
			throw new DbSqlException("Invalid JNDI datasource name: " + jndiName, nex);
		}
		this.username = user;
		this.password = pass;
	}

	public DataSourceConnectionProvider(final DataSource dataSource) {
		this.dataSource = dataSource;
		username = password = null;
	}

	public DataSourceConnectionProvider(final DataSource dataSource, final String user, final String pass) {
		this.dataSource = dataSource;
		this.username = user;
		this.password = pass;
	}

	@Override
	public void init() {}

	@Override
	public Connection getConnection() {
		try {
			if (username != null || password != null) {
				return dataSource.getConnection(username, password);
			} else {
				return dataSource.getConnection();
			}
		} catch (SQLException sex) {
			throw new DbSqlException("Invalid datasource connection", sex);
		}
	}

	@Override
	public void closeConnection(final Connection connection) {
		try {
			connection.close();
		} catch (SQLException sex) {
			// ignore
		}
	}

	@Override
	public void close() {}
}
