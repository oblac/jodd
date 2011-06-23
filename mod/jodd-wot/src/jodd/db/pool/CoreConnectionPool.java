// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.pool;

import jodd.db.DbSqlException;
import jodd.db.connection.ConnectionProvider;
import jodd.log.Log;

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

	private static final Log log = Log.getLogger(CoreConnectionPool.class);

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
	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * Specifies JDBC url.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	/**
	 * Specifies db username.
	 */
	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * Specifies db password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * Sets max number of connections.
	 */
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getMinConnections() {
		return minConnections;
	}

	/**
	 * Sets minimum number of open connections.
	 */
	public void setMinConnections(int minConnections) {
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
	public void setWaitIfBusy(boolean waitIfBusy) {
		this.waitIfBusy = waitIfBusy;
	}

	public long getValidationTimeout() {
		return validationTimeout;
	}

	/**
	 * Specifies number of milliseconds from connection creation
	 * when connection is considered as opened and valid.
	 */
	public void setValidationTimeout(long validationTimeout) {
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
	public void setValidationQuery(String validationQuery) {
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
	public void setValidateConnection(boolean validateConnection) {
		this.validateConnection = validateConnection;
	}

	// ---------------------------------------------------------------- init

	private ArrayList<ConnectionData> availableConnections, busyConnections;
	private boolean connectionPending;

	/**
	 * {@inheritDoc}
	 */
	public synchronized void init() {
		log.info("core connection pool initialization");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException cnfex) {
			throw new DbSqlException("Database driver not found: '" + driver + '\'', cnfex);
		}
		if (minConnections > maxConnections) {
			minConnections = maxConnections;
		}
		availableConnections = new ArrayList<ConnectionData>(maxConnections);
		busyConnections = new ArrayList<ConnectionData>(maxConnections);
		for (int i = 0; i < minConnections; i++) {
			try {
				Connection conn = DriverManager.getConnection(url, user, password); 
				availableConnections.add(new ConnectionData(conn));
			} catch (SQLException sex) {
				throw new DbSqlException("Unable to get database connection.", sex);
			}
		}
	}

	// ---------------------------------------------------------------- get/close

	/**
	 * {@inheritDoc}
	 */
	public synchronized Connection getConnection() {
		if (availableConnections == null) {
			throw new DbSqlException("Connection pool is not initialized.");
		}
		if (availableConnections.isEmpty() == false) {
			int lastIndex = availableConnections.size() - 1;
			ConnectionData existingConnection = availableConnections.get(lastIndex);
			availableConnections.remove(lastIndex);
			
			// If conn on available list is closed (e.g., it timed out), then remove it from available list
			// and repeat the process of obtaining a conn. Also wake up threads that were waiting for a
			// conn because maxConnection limit was reached.
			long now = System.currentTimeMillis();
			boolean isValid = isConnectionValid(existingConnection, now);
			if (isValid == false) {
				if (log.isDebugEnabled()) {
					log.debug("pooled connection is not valid, resetting");
				}
				notifyAll();				 // freed up a spot for anybody waiting
				return getConnection();
			} else {
				if (log.isDebugEnabled()) {
					log.debug("returning valid pooled connection");
				}
				busyConnections.add(existingConnection);
				existingConnection.lastUsed = now;
				return existingConnection.connection;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("no more available connections");
		}
		// no available connections
		if (((availableConnections.size() + busyConnections.size()) < maxConnections) && !connectionPending) {
			makeBackgroundConnection();
		} else if (!waitIfBusy) {
			throw new DbSqlException("Connection limit of " + maxConnections + " connections reached.");
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
	private boolean isConnectionValid(ConnectionData connectionData, long now) {
		if (validateConnection == false) {
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

	public synchronized void closeConnection(Connection connection) {
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
	public synchronized void close() {
		log.info("core connection pool shutdown");
		closeConnections(availableConnections);
		availableConnections = new ArrayList<ConnectionData>(maxConnections);
		closeConnections(busyConnections);
		busyConnections = new ArrayList<ConnectionData>(maxConnections);
	}

	private void closeConnections(ArrayList<ConnectionData> connections) {
		try {
			for (ConnectionData connectionData : connections) {
				Connection connection = connectionData.connection;
				if (!connection.isClosed()) {
					connection.close();
				}
			}
		} catch (SQLException sex) {
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

		ConnectionData(Connection connection) {
			this.connection = connection;
			this.lastUsed = System.currentTimeMillis();
		}

		@Override
		public boolean equals(Object o) {
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
		final int avaliableCount;
		final int busyCount;

		SizeSnapshot(int avaliableCount, int busyCount) {
			this.totalCount = avaliableCount + busyCount;
			this.avaliableCount = avaliableCount;
			this.busyCount = busyCount;
		}

		/**
		 * Returns total number of connections.
		 */
		public int getTotalCount() {
			return totalCount;
		}

		/**
		 * Returns number of availiable connections.
		 */
		public int getAvaliableCount() {
			return avaliableCount;
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
					", avaliable=" + avaliableCount +
					", busy=" + busyCount + '}';
		}
	}

}
