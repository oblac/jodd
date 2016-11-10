// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.http.net;

import jodd.http.HttpException;
import jodd.http.JoddHttp;
import jodd.http.HttpConnection;
import jodd.http.HttpConnectionProvider;
import jodd.http.HttpRequest;
import jodd.http.ProxyInfo;
import jodd.util.StringUtil;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket factory for HTTP proxy.
 */
public class SocketHttpConnectionProvider implements HttpConnectionProvider {

	protected ProxyInfo proxy = ProxyInfo.directProxy();

	/**
	 * Defines proxy to use for created sockets.
	 */
	@Override
	public void useProxy(ProxyInfo proxyInfo) {
		proxy = proxyInfo;
	}

	/**
	 * Creates new connection from current {@link jodd.http.HttpRequest request}.
	 *
	 * @see #createSocket(String, int, int)
	 */
	public HttpConnection createHttpConnection(HttpRequest httpRequest) throws IOException {
		SocketHttpConnection httpConnection;

		final boolean https = httpRequest.protocol().equalsIgnoreCase("https");

		if (https) {
			SSLSocket sslSocket = createSSLSocket(
				httpRequest.host(),
				httpRequest.port(),
				httpRequest.connectionTimeout(),
				httpRequest.trustAllCertificates());

			httpConnection = new SocketHttpSecureConnection(sslSocket);
		}
		else {
			Socket socket = createSocket(httpRequest.host(), httpRequest.port(), httpRequest.connectionTimeout());

			httpConnection = new SocketHttpConnection(socket);
		}

		// prepare connection config

		httpConnection.setTimeout(httpRequest.timeout());

		try {
			// additional socket initialization

			httpConnection.init();
		}
		catch (Throwable throwable) {  			// @wjw_add
			httpConnection.close();

			throw new HttpException(throwable);
		}

		return httpConnection;
	}

	/**
	 * Creates a socket using {@link #getSocketFactory(jodd.http.ProxyInfo) socket factory}.
	 */
	protected Socket createSocket(String host, int port, int connectionTimeout) throws IOException {
		SocketFactory socketFactory = getSocketFactory(proxy);

		if (connectionTimeout < 0) {
			return socketFactory.createSocket(host, port);
		}
		else {
			// creates unconnected socket
			Socket socket = socketFactory.createSocket();

			socket.connect(new InetSocketAddress(host, port), connectionTimeout);

			return socket;
		}
	}

	/**
	 * Creates a SSL socket. Enables default secure enabled protocols if specified..
	 */
	protected SSLSocket createSSLSocket(String host, int port, int connectionTimeout, boolean trustAll) throws IOException {
		SSLSocketFactory socketFactory;

		try {
			socketFactory = (SSLSocketFactory) getSSLSocketFactory(proxy, trustAll);
		}
		catch (Exception ex) {
			if (ex instanceof IOException) {
				throw (IOException) ex;
			} else {
				throw new IOException(ex.getMessage());
			}
		}

		SSLSocket sslSocket;

		if (connectionTimeout < 0) {
			sslSocket = (SSLSocket) socketFactory.createSocket(host, port);
		}
		else {
			// creates unconnected socket
			// unfortunately, this does not work always

//			sslSocket = (SSLSocket) socketFactory.createSocket();
//			sslSocket.connect(new InetSocketAddress(host, port), connectionTimeout);

			//
			// Note: SSLSocketFactory has several create() methods.
			// Those that take arguments all connect immediately
			// and have no options for specifying a connection timeout.
			//
			// So, we have to create a socket and connect it (with a
			// connection timeout), then have the SSLSocketFactory wrap
			// the already-connected socket.
			//
			Socket sock = new Socket();
			//sock.setSoTimeout(readTimeout);
			sock.connect(new InetSocketAddress(host, port), connectionTimeout);

			// wrap plain socket in an SSL socket
			sslSocket = (SSLSocket)socketFactory.createSocket(sock, host, port, true);
		}

		String enabledProtocols = JoddHttp.defaultSecureEnabledProtocols;

		if (enabledProtocols != null) {
			String[] values = StringUtil.splitc(enabledProtocols, ',');

			StringUtil.trimAll(values);

			sslSocket.setEnabledProtocols(values);
		}

		return sslSocket;
	}

	/**
	 * Returns new SSL socket factory. Called from {@link #createSSLSocket(String, int, int, boolean)}.
	 * May be overwritten to provide custom SSL socket factory by using e.g.
	 * <code>SSLContext</code>. By default returns default SSL socket factory for non-roxy connections or specified
	 * proxy socket factory based on proxy type.
	 */
	protected SocketFactory getSSLSocketFactory(ProxyInfo proxy, boolean trustAllCertificates) throws Exception {
		switch (proxy.getProxyType()) {
			case NONE:
				if (trustAllCertificates) {
					SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, TrustManagers.TRUST_ALL_CERTS, new java.security.SecureRandom());
					return sc.getSocketFactory();
				}
				else {
					return SSLSocketFactory.getDefault();
				}
			case HTTP:
				return new HTTPProxySocketFactory(proxy, true);
			case SOCKS4:
				return new Socks4ProxySocketFactory(proxy, true);
			case SOCKS5:
				return new Socks5ProxySocketFactory(proxy, true);
			default:
				return null;
		}
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
			default:
				return null;
		}
	}

}