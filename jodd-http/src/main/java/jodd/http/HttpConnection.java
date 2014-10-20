// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Http connection. Created by {@link HttpConnectionProvider}.
 */
public interface HttpConnection {

	/**
	 * Returns connection output stream.
	 */
	public OutputStream getOutputStream() throws IOException;

	/**
	 * Returns connection input stream.
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * Closes connection. Ignores all exceptions.
	 */
	public void close();

	/**
	 * Sets the timeout for connections, in milliseconds. With this option set to a non-zero timeout,
	 * connection will block for only this amount of time. If the timeout expires, an Exception is raised.
	 * The timeout must be > 0. A timeout of zero is interpreted as an infinite timeout.
	 */
	void setTimeout(int milliseconds);

}