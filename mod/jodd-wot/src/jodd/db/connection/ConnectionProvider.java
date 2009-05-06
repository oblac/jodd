// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.connection;

import java.sql.Connection;

/**
 * A generic strategy for obtaining JDBC connections.
 * <p>
 * Implementors might also implement connection pooling.
 * <p>
 * Implementations should provide a public default constructor.
 */
public interface ConnectionProvider {

	/**
	 * Initialize the connection provider. Properties are provided either
	 * with constructor either with bean setters.
	 */
	void init();


	/**
	 * Get a connection.
	 */
	Connection getConnection();

	/**
	 * Dispose of a used connection.
	 */
	void closeConnection(Connection conn);


	/**
	 * Closes a provider and releases all its resources.
	 */
	void close();

}
