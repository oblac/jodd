// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.pool;

import jodd.db.DbSqlException;
import jodd.db.connection.ConnectionProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A class for preallocating, recycling, and managing JDBC connections.<br><br>
 *
 * It uses threads for opening a new connextion. When no connection
 * available it will wait until a connection is released.<br><br>
 */
public class CoreConnectionPool implements Runnable, ConnectionProvider {

	// ---------------------------------------------------------------- properties

	private String driver;
	private String url;
	private String user;
	private String password;
	private int maxConnections = 10;
	private int minConnections = 5;
	private boolean waitIfBusy;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getMinConnections() {
		return minConnections;
	}

	public void setMinConnections(int minConnections) {
		this.minConnections = minConnections;
	}

	public boolean isWaitIfBusy() {
		return waitIfBusy;
	}

	public void setWaitIfBusy(boolean waitIfBusy) {
		this.waitIfBusy = waitIfBusy;
	}

	// ---------------------------------------------------------------- init

	private ArrayList<Connection> availableConnections, busyConnections;
	private boolean connectionPending;

	public CoreConnectionPool() {
	}

	public void init() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException cnfex) {
			throw new DbSqlException("Database driver not found: '" + driver + '\'', cnfex);
		}
		if (minConnections > maxConnections) {
			minConnections = maxConnections;
		}
		availableConnections = new ArrayList<Connection>(minConnections);
		busyConnections = new ArrayList<Connection>();
		for (int i = 0; i < minConnections; i++) {
			try {
				availableConnections.add(DriverManager.getConnection(url, user, password));
			} catch (SQLException sex) {
				throw new DbSqlException("Unable to get conn from jdbc driver.", sex);
			}
		}
	}

	// ---------------------------------------------------------------- get/close

	public synchronized Connection getConnection() {
		if (availableConnections.isEmpty() == false) {
			int lastIndex = availableConnections.size() - 1;
			Connection existingConnection = availableConnections.get(lastIndex);
			availableConnections.remove(lastIndex);
			
			// If conn on available list is closed (e.g., it timed out), then remove it from available list
			// and repeat the process of obtaining a conn. Also wake up threads that were waiting for a
			// conn because maxConnection limit was reached.
			boolean isClosed;
			try {
				isClosed = existingConnection.isClosed();
			} catch (SQLException sex) {
				throw new DbSqlException("Unable to check if database conn is closed.", sex);
			}
			if (isClosed) {
				notifyAll();				 // freed up a spot for anybody waiting
				return getConnection();
			} else {
				busyConnections.add(existingConnection);
				return existingConnection;
			}
		} else {
			// Three possible cases:
			// 1) You haven't reached maxConnections limit. So establish one in the background if there isn't
			//    already one pending, then wait for the next available conn (whether or not
			//    it was the newly established one).
			// 2) You reached maxConnections limit and waitIfBusy flag is false. Throw SQLException in such a case.
			// 3) You reached maxConnections limit and waitIfBusy flag is true. Then do the same thing as in second
			//    part of step 1: wait for next available conn.

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
		try {
			Thread connectThread = new Thread(this);
			connectThread.start();
		} catch (OutOfMemoryError oome) {
			// give up on new conn
		}
	}

	public void run() {
		try {
			Connection connection = DriverManager.getConnection(url, user, password);
			synchronized(this) {
				availableConnections.add(connection);
				connectionPending = false;
				notifyAll();
			}
		} catch (Exception ex) {	// SQLException or OutOfMemory
			// give up on new conn and wait for existing one to free up.
		}
	}

	public synchronized void closeConnection(Connection connection) {
		busyConnections.remove(connection);
		availableConnections.add(connection);
		notifyAll();		// wake up threads that are waiting for a conn
	}


	/**
	 * Returns connection number statistics in the following order:
	 * <ol>
	 * <li>total connections</li>
	 * <li>availiable connections</li>
	 * <li>busy connections</li>
	 * </ol>
	 */
	public synchronized int[] getConnectionsCount() {
		return new int[] {availableConnections.size() + busyConnections.size(), availableConnections.size(), busyConnections.size()};
	}

	// ---------------------------------------------------------------- close

	/**
	 * Close all the connections. Use with caution: be sure no connections are in
	 * use before calling. Note that you are not <I>required</I> to call this
	 * when done with a ConnectionPool, since connections are guaranteed to be
	 * closed when garbage collected. But this method gives more control
	 * regarding when the connections are closed.
	 */
	public synchronized void close() {
		closeConnections(availableConnections);
		availableConnections = new ArrayList<Connection>();
		closeConnections(busyConnections);
		busyConnections = new ArrayList<Connection>();
	}

	private void closeConnections(ArrayList<Connection> connections) {
		try {
			for (Connection connection : connections) {
				if (!connection.isClosed()) {
					connection.close();
				}
			}
		} catch (SQLException sqle) {
			// Ignore errors; garbage collect anyhow
		}
	}
}
