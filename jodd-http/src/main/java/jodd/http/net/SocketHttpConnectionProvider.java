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

import jodd.http.HttpConnection;
import jodd.http.HttpConnectionProvider;
import jodd.http.HttpException;
import jodd.http.HttpRequest;
import jodd.http.ProxyInfo;
import jodd.util.StringUtil;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Socket factory for HTTP proxy.
 */
public class SocketHttpConnectionProvider implements HttpConnectionProvider {

	protected ProxyInfo proxy = ProxyInfo.directProxy();
	protected String secureEnabledProtocols = System.getProperty("https.protocols");
	protected String sslProtocol = "TLSv1.1";

	/**
	 * Defines proxy to use for created sockets.
	 */
	@Override
	public void useProxy(final ProxyInfo proxyInfo) {
		proxy = proxyInfo;
	}

	/**
	 * CSV of default enabled secured protocols. By default the value is
	 * read from system property <code>https.protocols</code>.
	 */
	public void setSecuredProtocols(final String secureEnabledProtocols) {
		this.secureEnabledProtocols = secureEnabledProtocols;
	}

	/**
	 * Returns current SSL protocol used.
	 */
	public String getSslProtocol() {
		return sslProtocol;
	}

	/**
	 * Sets default SSL protocol to use. One of "SSL", "TLSv1.2", "TLSv1.1", "TLSv1".
	 */
	public SocketHttpConnectionProvider setSslProtocol(final String sslProtocol) {
		this.sslProtocol = sslProtocol;
		return this;
	}

	/**
	 * Creates new connection from current {@link jodd.http.HttpRequest request}.
	 *
	 * @see #createSocket(String, int, int)
	 */
	@Override
	public HttpConnection createHttpConnection(final HttpRequest httpRequest) throws IOException {
		final SocketHttpConnection httpConnection;

		final boolean https = httpRequest.protocol().equalsIgnoreCase("https");

		if (https) {
			SSLSocket sslSocket = createSSLSocket(
				httpRequest.host(),
				httpRequest.port(),
				httpRequest.connectionTimeout(),
				httpRequest.trustAllCertificates(),
				httpRequest.verifyHttpsHost()
			);

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
	 * Creates a socket using socket factory.
	 */
	protected Socket createSocket(final String host, final int port, final int connectionTimeout) throws IOException {
		SocketFactory socketFactory = getSocketFactory(proxy, false, false);

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
	 * Creates a SSL socket. Enables default secure enabled protocols if specified.
	 */
	protected SSLSocket createSSLSocket(
		final String host, final int port, final int connectionTimeout,
		final boolean trustAll, final boolean verifyHttpsHost) throws IOException {

		SocketFactory socketFactory = getSocketFactory(proxy, true, trustAll);

		Socket socket;

		if (connectionTimeout < 0) {
			socket = socketFactory.createSocket(host, port);
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
			socket = new Socket();
			//sock.setSoTimeout(readTimeout);
			socket.connect(new InetSocketAddress(host, port), connectionTimeout);

			// continue to wrap this plain socket with ssl socket...
		}


		// wrap plain socket in an SSL socket

		SSLSocket sslSocket;

		if (socket instanceof SSLSocket) {
			sslSocket = (SSLSocket) socket;
		}
		else {
			if (socketFactory instanceof SSLSocketFactory) {
				sslSocket = (SSLSocket) ((SSLSocketFactory)socketFactory).createSocket(socket, host, port, true);
			}
			else {
				sslSocket = (SSLSocket) (getDefaultSSLSocketFactory(trustAll)).createSocket(socket, host, port, true);
			}
		}

		// sslSocket is now ready

		if (secureEnabledProtocols != null) {
			final String[] values = StringUtil.splitc(secureEnabledProtocols, ',');

			StringUtil.trimAll(values);

			sslSocket.setEnabledProtocols(values);
		}

		// set SSL parameters to allow host name verifier

		if (verifyHttpsHost) {
			final SSLParameters sslParams = new SSLParameters();

			sslParams.setEndpointIdentificationAlgorithm("HTTPS");

			sslSocket.setSSLParameters(sslParams);
		}

		return sslSocket;
	}

	/**
	 * Returns default SSL socket factory allowing setting trust managers.
	 */
	protected SSLSocketFactory getDefaultSSLSocketFactory(final boolean trustAllCertificates) throws IOException {
		if (trustAllCertificates) {
			try {
				SSLContext sc = SSLContext.getInstance(sslProtocol);
				sc.init(null, TrustManagers.TRUST_ALL_CERTS, new java.security.SecureRandom());
				return sc.getSocketFactory();
			}
			catch (NoSuchAlgorithmException | KeyManagementException e) {
				throw new IOException(e);
			}
		} else {
			return (SSLSocketFactory) SSLSocketFactory.getDefault();
		}
	}

	/**
	 * Returns socket factory based on proxy type and SSL requirements.
	 */
	protected SocketFactory getSocketFactory(final ProxyInfo proxy, final boolean ssl, final boolean trustAllCertificates) throws IOException {
		switch (proxy.getProxyType()) {
			case NONE:
				if (ssl) {
					return getDefaultSSLSocketFactory(trustAllCertificates);
				}
				else {
					return SocketFactory.getDefault();
				}
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