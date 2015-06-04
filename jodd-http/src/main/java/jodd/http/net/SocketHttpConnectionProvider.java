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

import jodd.http.JoddHttp;
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
	 * @see #createSocket(String, int)
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
	 * Creates a socket using {@link #getSocketFactory(jodd.http.ProxyInfo) socket factory}.
	 */
	protected Socket createSocket(String host, int port) throws IOException {
		SocketFactory socketFactory = getSocketFactory(proxy);

		return socketFactory.createSocket(host, port);
	}

	/**
	 * Creates a SSL socket. Enables default secure enabled protocols if specified..
	 */
	protected SSLSocket createSSLSocket(String host, int port) throws IOException {
		SocketFactory socketFactory;
		try {
			socketFactory = getSSLSocketFactory();
		}
		catch (Exception ex) {
			if (ex instanceof IOException) {
				throw (IOException) ex;
			} else {
				throw new IOException(ex.getMessage());
			}
		}

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
	 * Returns new SSL socket factory. Called from {@link #createSSLSocket(String, int)}.
	 * May be overwritten to provide custom SSL socket factory by using e.g.
	 * <code>SSLContext</code>. By default returns default SSL socket factory.
	 */
	protected SocketFactory getSSLSocketFactory() throws Exception {
		return SSLSocketFactory.getDefault();
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