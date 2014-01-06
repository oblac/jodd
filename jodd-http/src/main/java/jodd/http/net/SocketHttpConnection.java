// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http.net;

import jodd.http.HttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Socket-based {@link jodd.http.HttpConnection}.
 * @see SocketHttpConnectionProvider
 */
public class SocketHttpConnection implements HttpConnection {

	protected final Socket socket;

	public SocketHttpConnection(Socket socket) {
		this.socket = socket;
	}

	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException ignore) {
		}
	}

	/**
	 * Returns <code>Socket</code> used by this connection.
	 */
	public Socket getSocket() {
		return socket;
	}
}