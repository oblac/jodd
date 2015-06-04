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
import jodd.http.ProxyInfo;
import jodd.util.Base64;

import javax.net.SocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Socket factory for HTTP proxy.
 */
public class HTTPProxySocketFactory extends SocketFactory {

	private final ProxyInfo proxy;

	public HTTPProxySocketFactory(ProxyInfo proxy) {
		this.proxy = proxy;
	}

	public Socket createSocket(String host, int port) throws IOException {
		return createHttpProxySocket(host, port);
	}

	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
		return createHttpProxySocket(host, port);
	}

	public Socket createSocket(InetAddress host, int port) throws IOException {
		return createHttpProxySocket(host.getHostAddress(), port);
	}

	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return createHttpProxySocket(address.getHostAddress(), port);
	}

	private Socket createHttpProxySocket(String host, int port) {
		Socket socket = null;
		String proxyAddress = proxy.getProxyAddress();
		int proxyPort = proxy.getProxyPort();

		try {

			socket = new Socket(proxyAddress, proxyPort);
			String hostport = "CONNECT " + host + ":" + port;
			String proxyLine;
			String username = proxy.getProxyUsername();

			if (username == null) {
				proxyLine = "";
			} else {
				String password = proxy.getProxyPassword();
				proxyLine =
						"\r\nProxy-Authorization: Basic "
						+ Base64.encodeToString((username + ":" + password));
			}
			socket.getOutputStream().write(
					(hostport + " HTTP/1.1\r\nHost: "
					+ hostport + proxyLine + "\r\n\r\n").getBytes("UTF-8"));

			InputStream in = socket.getInputStream();
			StringBuilder recv = new StringBuilder(100);
			int nlchars = 0;

			while (true) {
				char c = (char) in.read();
				recv.append(c);
				if (recv.length() > 1024) {
					throw new HttpException(ProxyInfo.ProxyType.HTTP, "Received header longer then 1024 chars");
				}
				if (c == -1) {
					throw new HttpException(ProxyInfo.ProxyType.HTTP, "Invalid response");
				}
				if ((nlchars == 0 || nlchars == 2) && c == '\r') {
					nlchars++;
				} else if ((nlchars == 1 || nlchars == 3) && c == '\n') {
					nlchars++;
				} else {
					nlchars = 0;
				}
				if (nlchars == 4) {
					break;
				}
			}

			String recvStr = recv.toString();

			BufferedReader br = new BufferedReader(new StringReader(recvStr));
			String response = br.readLine();

			if (response == null) {
				throw new HttpException(ProxyInfo.ProxyType.HTTP, "Empty proxy response");
			}

			Matcher m = RESPONSE_PATTERN.matcher(response);
			if (!m.matches()) {
				throw new HttpException(ProxyInfo.ProxyType.HTTP, "Unexpected proxy response");
			}

			int code = Integer.parseInt(m.group(1));

			if (code != HttpURLConnection.HTTP_OK) {
				throw new HttpException(ProxyInfo.ProxyType.HTTP, "Invalid code");
			}

			return socket;
		} catch (RuntimeException rtex) {
			closeSocket(socket);
			throw rtex;
		} catch (Exception ex) {
			closeSocket(socket);
			throw new HttpException(ProxyInfo.ProxyType.HTTP, ex.toString(), ex);
		}

	}

	/**
	 * Closes socket silently.
	 */
	private void closeSocket(Socket socket) {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (Exception ignore) {
		}
	}

	private static final Pattern RESPONSE_PATTERN =
			Pattern.compile("HTTP/\\S+\\s(\\d+)\\s(.*)\\s*");
}