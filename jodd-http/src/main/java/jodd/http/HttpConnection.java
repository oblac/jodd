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

}