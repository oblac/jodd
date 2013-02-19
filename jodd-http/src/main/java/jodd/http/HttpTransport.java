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
 * Socket adapter for {@link HttpRequest}.
 */
public class HttpTransport {

	protected Socket socket;
	protected HttpRequest httpRequest;

 	/**
	 * Creates new socket from current host and port.
	 */
	public Socket open(HttpRequest httpRequest) throws IOException {
		this.httpRequest = httpRequest;

		if (httpRequest.protocol().equals("https")) {
			SocketFactory ssocketFactory = SSLSocketFactory.getDefault();
			SSLSocket sslSocket = (SSLSocket) ssocketFactory.createSocket(httpRequest.host(), 443);
			sslSocket.startHandshake();

			this.socket = sslSocket;
		}
		else {
			this.socket = new Socket(httpRequest.host(), httpRequest.port());
		}

		return socket;
	}

	/**
	 * Opens sockets output stream and sends request data to it.
	 * Returns parsed response.
	 */
	public HttpResponse send() throws IOException {

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