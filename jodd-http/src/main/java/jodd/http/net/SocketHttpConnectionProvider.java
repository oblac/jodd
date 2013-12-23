// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http.net;

import jodd.http.HttpConnection;
import jodd.http.HttpConnectionProvider;
import jodd.http.HttpRequest;
import jodd.http.ProxyInfo;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

/**
 * Socket factory for HTTP proxy.
 */
public class SocketHttpConnectionProvider implements HttpConnectionProvider {

	protected ProxyInfo proxy = ProxyInfo.directProxy();

	/**
	 * Defines proxy to use for created sockets.
	 */
	public void useProxy(ProxyInfo proxyInfo) {
		proxy = proxyInfo;
	}

	/**
	 * Creates new connection from current {@link jodd.http.HttpRequest request}.
	 *
	 * @see #createSocket(javax.net.SocketFactory, String, int)
	 */
	public HttpConnection createHttpConnection(HttpRequest httpRequest) throws IOException {
		Socket socket;

		if (httpRequest.protocol().equals("https")) {
			SSLSocket sslSocket = (SSLSocket) createSocket(
					SSLSocketFactory.getDefault(), httpRequest.host(), 443);

			sslSocket.startHandshake();

			socket = sslSocket;
		} else {
			SocketFactory socketFactory = getSocketFactory(proxy);

			socket = createSocket(socketFactory, httpRequest.host(), httpRequest.port());
		}

		return new SocketHttpConnection(socket);
	}

	/**
	 * Creates a socket with provided socket factory.
	 */
	protected Socket createSocket(SocketFactory socketFactory, String host, int port) throws IOException {
		return socketFactory.createSocket(host, port);
	}

	/**
	 * Returns socket factory based on proxy type.
	 */
	public SocketFactory getSocketFactory(ProxyInfo proxy) {
		switch (proxy.getProxyType()) {
			case NONE:
				return SocketFactory.getDefault();
			case HTTP:
				return new HTTPProxySocketFactory(proxy);
			case SOCKS4:
				return new Socks4ProxySocketFactory(proxy);
			case SOCKS5:
				return new Socks5ProxySocketFactory(proxy);
		}
		return null;
	}

}