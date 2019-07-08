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

package jodd.db.pool;

import jodd.db.DbSqlException;
import jodd.db.connection.ConnectionProvider;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * A class for pre-allocating, recycling, and managing JDBC connections.
 * <p>
 * It uses threads for opening a new connection. When no connection
 * available it will wait until a connection is released.
 */
public class CoreConnectionPool implements Runnable, ConnectionProvider {

	private static final Logger log = LoggerFactory.getLogger(CoreConnectionPool.class);

	// ---------------------------------------------------------------- properties

	private static final String DEFAULT_VALIDATION_QUERY = "select 1";

	private String driver;
	private String url;
	private String user;
	private String password;
	private int maxConnections = 10;
	private int minConnections = 5;
	private boolean waitIfBusy;
	private boolean validateConnection = true;
	private long validationTimeout = 18000000L;		// 5 hours
	private String validationQuery;

	public String getDriver() {
		return driver;
	}

	/**
	 * Specifies driver class name.
	 */
	public void setDriver(final String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * Specifies JDBC url.
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	/**
	 * Specifies db username.
	 */
	public void setUser(final String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * Specifies db password.
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * Sets max number of connections.
	 */
	public void setMaxConnections(final int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getMinConnections() {
		return minConnections;
	}

	/**
	 * Sets minimum number of open connections.
	 */
	public void setMinConnections(final int minConnections) {
		this.minConnections = minConnections;
	}

	public boolean isWaitIfBusy() {
		return waitIfBusy;
	}

	/**
	 * Sets if pool should wait for connection to be freed when none
	 * is available. If wait for busy is <code>false</code>
	 * exception will be thrown when max connection is reached.
	 */
	public void setWaitIfBusy(final boolean waitIfBusy) {
		this.waitIfBusy = waitIfBusy;
	}

	public long getValidationTimeout() {
		return validationTimeout;
	}

	/**
	 * Specifies number of milliseconds from connection creation
	 * when connection is considered as opened and valid.
	 */
	public void setValidationTimeout(final long validationTimeout) {
		this.validationTimeout = validationTimeout;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	/**
	 * Specifies query to be used for validating connections.
	 * If set to <code>null</code> validation will be performed
	 * by invoking <code>Connection#isClosed</code> method.
	 */
	public void setValidationQuery(final String validationQuery) {
		this.validationQuery = validationQuery;
	}

	/**
	 * Sets default validation query (select 1);
	 */
	public void setDefaultValidationQuery() {
		this.validationQuery = DEFAULT_VALIDATION_QUERY;
	}

	public boolean isValidateConnection() {
		return validateConnection;
	}

	/**
	 * Specifies if connections should be validated before returned.
	 */
	public void setValidateConnection(final boolean validateConnection) {
		this.validateConnection = validateConnection;
	}

	// ---------------------------------------------------------------- init

	private ArrayList<ConnectionData> availableConnections, busyConnections;
	private boolean connectionPending;
	private boolean initialised;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void init() {
		if (initialised) {
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("Core connection pool initialization");
		}
		try {
			Class.forName(driver);
		}
		catch (ClassNotFoundException cnfex) {
			throw new DbSqlException("Database driver not found: " + driver, cnfex);
		}

		if (minConnections > maxConnections) {
			minConnections = maxConnections;
		}
		availableConnections = new ArrayList<>(maxConnections);
		busyConnections = new ArrayList<>(maxConnections);

		for (int i = 0; i < minConnections; i++) {
			try {
				Connection conn = DriverManager.getConnection(url, user, password); 
				availableConnections.add(new ConnectionData(conn));
			} catch (SQLException sex) {
				throw new DbSqlException("No database connection", sex);
			}
		}
		initialised = true;
	}

	// ---------------------------------------------------------------- get/close

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Connection getConnection() {
		if (availableConnections == null) {
			throw new DbSqlException("Connection pool is not initialized");
		}
		if (!availableConnections.isEmpty()) {
			int lastIndex = availableConnections.size() - 1;
			ConnectionData existingConnection = availableConnections.get(lastIndex);
			availableConnections.remove(lastIndex);
			
			// If conn on available list is closed (e.g., it timed out), then remove it from available list
			// and repeat the process of obtaining a conn. Also wake up threads that were waiting for a
			// conn because maxConnection limit was reached.
			long now = System.currentTimeMillis();
			boolean isValid = isConnectionValid(existingConnection, now);
			if (!isValid) {
				if (log.isDebugEnabled()) {
					log.debug("Pooled connection not valid, resetting");
				}

				notifyAll();				 // freed up a spot for anybody waiting
				return getConnection();
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Returning valid pooled connection");
				}

				busyConnections.add(existingConnection);
				existingConnection.lastUsed = now;
				return existingConnection.connection;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("No more available connections");
		}

		// no available connections
		if (((availableConnections.size() + busyConnections.size()) < maxConnections) && !connectionPending) {
			makeBackgroundConnection();
		} else if (!waitIfBusy) {
			throw new DbSqlException("Connection limit reached: " + maxConnections);
		}
		// wait for either a new conn to be established (if you called makeBackgroundConnection) or for
		// an existing conn to be freed up.
		try {
			wait();
		} catch (InterruptedException ie) {
			// ignore
		}
		// someone freed up a conn, so try again.
		return getConnection();
	}

	/**
	 * Checks if existing connection is valid and available. It may happens
	 * that if connection is not used for a while it becomes inactive,
	 * although not technically closed.
	 */
	private boolean isConnectionValid(final ConnectionData connectionData, final long now) {
		if (!validateConnection) {
			return true;
		}
		
		if (now < connectionData.lastUsed + validationTimeout) {
			return true;
		}

		Connection conn = connectionData.connection;

		if (validationQuery == null) {
			try {
				return !conn.isClosed();
			} catch (SQLException sex) {
				return false;
			}
		}
		
		boolean valid = true;
		Statement st = null;
		try {
			st = conn.createStatement();
			st.execute(validationQuery);
		} catch (SQLException sex) {
			valid = false;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException ignore) {
				}
			}
		}
		return valid;
	}

	/**
	 * You can't just make a new conn in the foreground when none are
	 * available, since this can take several seconds with a slow network
	 * conn. Instead, start a thread that establishes a new conn,
	 * then wait. You get woken up either when the new conn is established
	 * or if someone finishes with an existing conn.
	 */
	private void makeBackgroundConnection() {
		connectionPending = true;
		Thread connectThread = new Thread(this);
		connectThread.start();
	}

	@Override
	public void run() {
		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			synchronized(this) {
				availableConnections.add(new ConnectionData(connection));
				connectionPending = false;
				notifyAll();
			}
		} catch (Exception ex) {
			// give up on new conn and wait for existing one to free up.
		}
	}

	@Override
	public synchronized void closeConnection(final Connection connection) {
		ConnectionData connectionData = new ConnectionData(connection);
		busyConnections.remove(connectionData);
		availableConnections.add(connectionData);
		notifyAll();		// wake up threads that are waiting for a conn
	}


	// ---------------------------------------------------------------- close

	/**
	 * Close all the connections. Use with caution: be sure no connections are in
	 * use before calling. Note that you are not <i>required</i> to call this
	 * when done with a ConnectionPool, since connections are guaranteed to be
	 * closed when garbage collected. But this method gives more control
	 * regarding when the connections are closed.
	 */
	@Override
	public synchronized void close() {
		if (log.isInfoEnabled()) {
			log.info("Core connection pool shutdown");
		}
		closeConnections(availableConnections);
		availableConnections = new ArrayList<>(maxConnections);
		closeConnections(busyConnections);
		busyConnections = new ArrayList<>(maxConnections);
	}

	private void closeConnections(final ArrayList<ConnectionData> connections) {
		if (connections == null) {
			return;
		}
		try {
			for (ConnectionData connectionData : connections) {
				Connection connection = connectionData.connection;
				if (!connection.isClosed()) {
					connection.close();
				}
			}
		} catch (SQLException ignore) {
			// Ignore errors; garbage collect anyhow
		}
	}

	// ---------------------------------------------------------------- conn data

	/**
	 * Connection data with last used timestamp.
	 */
	class ConnectionData {
		final Connection connection;
		long lastUsed;

		ConnectionData(final Connection connection) {
			this.connection = connection;
			this.lastUsed = System.currentTimeMillis();
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			ConnectionData that = (ConnectionData) o;
			return connection.equals(that.connection);
		}

		@Override
		public int hashCode() {
			return connection.hashCode();
		}
	}

	// ---------------------------------------------------------------- stats

	/**
	 * Returns connection stats.
	 */
	public synchronized SizeSnapshot getConnectionsCount() {
		return new SizeSnapshot(availableConnections.size(), busyConnections.size());
	}

	/**
	 * Just a statistic class.
	 */
	public static class SizeSnapshot {
		final int totalCount;
		final int availableCount;
		final int busyCount;

		SizeSnapshot(final int availableCount, final int busyCount) {
			this.totalCount = availableCount + busyCount;
			this.availableCount = availableCount;
			this.busyCount = busyCount;
		}

		/**
		 * Returns total number of connections.
		 */
		public int getTotalCount() {
			return totalCount;
		}

		/**
		 * Returns number of available connections.
		 */
		public int getAvailableCount() {
			return availableCount;
		}

		/**
		 * Returns number of busy connections.
		 */
		public int getBusyCount() {
			return busyCount;
		}

		@Override
		public String toString() {
			return "Connections count: {total=" + totalCount +
					", available=" + availableCount +
					", busy=" + busyCount + '}';
		}
	}

}
