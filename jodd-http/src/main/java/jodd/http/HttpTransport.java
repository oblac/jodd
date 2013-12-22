// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Socket adapter used by {@link HttpRequest}.
 */
public class HttpTransport {

	protected Socket socket;

 	/**
	 * Creates new socket from current {@link jodd.http.HttpRequest request}.
	 * @see #createSocket(javax.net.SocketFactory, String, int)
	 */
	public Socket open(HttpRequest httpRequest) throws IOException {
		if (httpRequest.protocol().equals("https")) {
			SSLSocket sslSocket = (SSLSocket) createSocket(
					SSLSocketFactory.getDefault(), httpRequest.host(), 443);
			sslSocket.startHandshake();

			this.socket = sslSocket;
		}
		else {
			this.socket = createSocket(
					SocketFactory.getDefault(), httpRequest.host(), httpRequest.port());
		}

		return socket;
	}

	/**
	 * Creates a socket with provided socket factory.
	 */
	protected Socket createSocket(SocketFactory socketFactory, String host, int port) throws IOException {
		return socketFactory.createSocket(host, port);
	}

	/**
	 * Opens sockets output stream and sends request data to it.
	 * Returns parsed response.
	 */
	public HttpResponse send(HttpRequest httpRequest) throws IOException {
		OutputStream outputStream = socket.getOutputStream();

		httpRequest.sendTo(outputStream);

		InputStream inputStream = socket.getInputStream();

		return HttpResponse.readFrom(inputStream);
	}

	/**
	 * Closes socket.
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException ignore) {
		}
	}

	/**
	 * Returns used socket.
	 */
	public Socket getSocket() {
		return socket;
	}

}