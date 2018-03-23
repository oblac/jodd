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

import javax.net.SocketFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Socket factory for SOCKS4 proxy. This proxy does not do password authentication.
 *
 * See: http://www.openssh.com/txt/socks5.protocol for more details.
 */
public class Socks4ProxySocketFactory extends SocketFactory {

	private final ProxyInfo proxy;

	public Socks4ProxySocketFactory(final ProxyInfo proxy) {
		this.proxy = proxy;
	}

	@Override
	public Socket createSocket(final String host, final int port) {
		return createSocks4ProxySocket(host, port);
	}

	@Override
	public Socket createSocket(final String host, final int port, final InetAddress localHost, final int localPort) {
		return createSocks4ProxySocket(host, port);
	}

	@Override
	public Socket createSocket(final InetAddress host, final int port) {
		return createSocks4ProxySocket(host.getHostAddress(), port);
	}

	@Override
	public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) {
		return createSocks4ProxySocket(address.getHostAddress(), port);
	}

	/**
	 * Connects to the SOCKS4 proxy and returns proxified socket.
	 */
	private Socket createSocks4ProxySocket(final String host, final int port) {
		Socket socket = null;
		String proxyHost = proxy.getProxyAddress();
		int proxyPort = proxy.getProxyPort();
		String user = proxy.getProxyUsername();

		try {
			socket = new Socket(proxyHost, proxyPort);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			socket.setTcpNoDelay(true);

			byte[] buf = new byte[1024];

			// 1) CONNECT

			int index = 0;
			buf[index++] = 4;
			buf[index++] = 1;

			buf[index++] = (byte) (port >>> 8);
			buf[index++] = (byte) (port & 0xff);

			InetAddress addr = InetAddress.getByName(host);
			byte[] byteAddress = addr.getAddress();
			for (byte byteAddres : byteAddress) {
				buf[index++] = byteAddres;
			}

			if (user != null) {
				System.arraycopy(user.getBytes(), 0, buf, index, user.length());
				index += user.length();
			}
			buf[index++] = 0;
			out.write(buf, 0, index);

			// 2) RESPONSE

			int len = 6;
			int s = 0;
			while (s < len) {
				int i = in.read(buf, s, len - s);
				if (i <= 0) {
					throw new HttpException(ProxyInfo.ProxyType.SOCKS4, "stream is closed");
				}
				s += i;
			}
			if (buf[0] != 0) {
				throw new HttpException(ProxyInfo.ProxyType.SOCKS4, "proxy returned VN " + buf[0]);
			}
			if (buf[1] != 90) {
				try {
					socket.close();
				} catch (Exception ignore) {
				}
				throw new HttpException(ProxyInfo.ProxyType.SOCKS4, "proxy returned CD " + buf[1]);
			}

			byte[] temp = new byte[2];
			in.read(temp, 0, 2);

			return socket;
		} catch (RuntimeException rtex) {
			closeSocket(socket);
			throw rtex;
		} catch (Exception ex) {
			closeSocket(socket);
			throw new HttpException(ProxyInfo.ProxyType.SOCKS4, ex.toString(), ex);
		}
	}

	/**
	 * Closes socket silently.
	 */
	private void closeSocket(final Socket socket) {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (Exception ignore) {
		}
	}
}