// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http.net;

import jodd.JoddHttp;
import jodd.http.HttpConnection;
import jodd.http.HttpConnectionProvider;
import jodd.http.HttpRequest;
import jodd.http.ProxyInfo;
import jodd.util.StringUtil;

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

		if (httpRequest.protocol().equalsIgnoreCase("https")) {
			SSLSocket sslSocket = createSSLSocket(httpRequest.host(), httpRequest.port());

			sslSocket.startHandshake();

			socket = sslSocket;
		} else {
			socket = createSocket(httpRequest.host(), httpRequest.port());
		}

		return new SocketHttpConnection(socket);
	}

	/**
	 * Creates a socket using socket factory.
	 */
	protected Socket createSocket(String host, int port) throws IOException {
		SocketFactory socketFactory = getSocketFactory(proxy);

		return socketFactory.createSocket(host, port);
	}

	/**
	 * Creates a SSL socket. Enables default secure enabled protocols if specified..
	 */
	protected SSLSocket createSSLSocket(String host, int port) throws IOException {
		SocketFactory socketFactory = SSLSocketFactory.getDefault();

		SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(host, port);

		String enabledProtocols = JoddHttp.defaultSecureEnabledProtocols;

		if (enabledProtocols != null) {
			String[] values = StringUtil.splitc(enabledProtocols, ',');

			StringUtil.trimAll(values);

			sslSocket.setEnabledProtocols(values);
		}

		return sslSocket;
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