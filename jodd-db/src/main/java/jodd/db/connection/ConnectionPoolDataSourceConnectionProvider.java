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

	public ConnectionPoolDataSourceConnectionProvider(final ConnectionPoolDataSource cpds) {
		this.cpds = cpds;
	}

	// ---------------------------------------------------------------- init/close

	@Override
	public void init() {
	}

	@Override
	public void close() {
		cpds = null;
	}

	// ---------------------------------------------------------------- get/free

	@Override
	public Connection getConnection() {
		PooledConnection pconn;
		try {
			pconn = cpds.getPooledConnection();
		} catch (SQLException sex) {
			throw new DbSqlException("Invalid pooled connection", sex);
		}
		try {
			return pconn.getConnection();
		} catch (SQLException sex) {
			throw new DbSqlException("Invalid pooled connection", sex);
		}
	}

	@Override
	public void closeConnection(final Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sex) {
			// ignore
		}
	}
}
