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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Socket factory for SOCKS5 proxy.
 *
 * See: http://www.ietf.org/rfc/rfc1928.txt
 */
public class Socks5ProxySocketFactory extends SocketFactory {

	private final ProxyInfo proxy;

	public Socks5ProxySocketFactory(final ProxyInfo proxy) {
		this.proxy = proxy;
	}

	@Override
	public Socket createSocket(final String host, final int port) {
		return createSocks5ProxySocket(host, port);
	}

	@Override
	public Socket createSocket(final String host, final int port, final InetAddress localHost, final int localPort) {
		return createSocks5ProxySocket(host, port);
	}

	@Override
	public Socket createSocket(final InetAddress host, final int port) {
		return createSocks5ProxySocket(host.getHostAddress(), port);
	}

	@Override
	public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) {
		return createSocks5ProxySocket(address.getHostAddress(), port);
	}

	private Socket createSocks5ProxySocket(final String host, final int port) {
		Socket socket = null;
		String proxyAddress = proxy.getProxyAddress();
		int proxyPort = proxy.getProxyPort();
		String user = proxy.getProxyUsername();
		String passwd = proxy.getProxyPassword();

		try {
			socket = new Socket(proxyAddress, proxyPort);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			socket.setTcpNoDelay(true);

			byte[] buf = new byte[1024];
			int index = 0;

			// 1) VERSION IDENT/METHOD SELECTION

			buf[index++] = 5;

			buf[index++] = 2;
			buf[index++] = 0; // NO AUTHENTICATION REQUIRED
			buf[index++] = 2; // USERNAME/PASSWORD

			out.write(buf, 0, index);

			// 2) RESPONSE
			// in.read(buf, 0, 2);
			fill(in, buf, 2);

			boolean check = false;
			switch ((buf[1]) & 0xff) {
				case 0: // NO AUTHENTICATION REQUIRED
					check = true;
					break;
				case 2: // USERNAME/PASSWORD
					if (user == null || passwd == null) {
						break;
					}

					// 3) USER/PASS REQUEST

					index = 0;
					buf[index++] = 1;
					buf[index++] = (byte) (user.length());
					System.arraycopy(user.getBytes(), 0, buf, index, user.length());

					index += user.length();
					buf[index++] = (byte) (passwd.length());
					System.arraycopy(passwd.getBytes(), 0, buf, index, passwd.length());
					index += passwd.length();

					out.write(buf, 0, index);

					// 4) RESPONSE, VERIFIED
					// in.read(buf, 0, 2);
					fill(in, buf, 2);
					if (buf[1] == 0) {
						check = true;
					}
					break;
				default:
			}

			if (!check) {
				try {
					socket.close();
				} catch (Exception ignore) {
				}
				throw new HttpException(ProxyInfo.ProxyType.SOCKS5, "check failed");
			}

			// 5) CONNECT

			index = 0;
			buf[index++] = 5;
			buf[index++] = 1; // CONNECT
			buf[index++] = 0;

			byte[] hostb = host.getBytes();
			int len = hostb.length;
			buf[index++] = 3; // DOMAINNAME
			buf[index++] = (byte) (len);
			System.arraycopy(hostb, 0, buf, index, len);

			index += len;
			buf[index++] = (byte) (port >>> 8);
			buf[index++] = (byte) (port & 0xff);

			out.write(buf, 0, index);

			// 6) RESPONSE

			// in.read(buf, 0, 4);
			fill(in, buf, 4);

			if (buf[1] != 0) {
				try {
					socket.close();
				} catch (Exception ignore) {
				}
				throw new HttpException(ProxyInfo.ProxyType.SOCKS5, "proxy returned " + buf[1]);
			}

			switch (buf[3] & 0xff) {
				case 1:
					// in.read(buf, 0, 6);
					fill(in, buf, 6);
					break;
				case 3:
					// in.read(buf, 0, 1);
					fill(in, buf, 1);
					// in.read(buf, 0, buf[0]+2);
					fill(in, buf, (buf[0] & 0xff) + 2);
					break;
				case 4:
					// in.read(buf, 0, 18);
					fill(in, buf, 18);
					break;
				default:
			}
			return socket;
		} catch (RuntimeException rttex) {
			closeSocket(socket);
			throw rttex;
		} catch (Exception ex) {
			closeSocket(socket);
			throw new HttpException(ProxyInfo.ProxyType.SOCKS5, ex.toString(), ex);
		}
	}

	private void fill(final InputStream in, final byte[] buf, final int len) throws IOException {
		int s = 0;
		while (s < len) {
			int i = in.read(buf, s, len - s);
			if (i <= 0) {
				throw new HttpException(ProxyInfo.ProxyType.SOCKS5, "stream is closed");
			}
			s += i;
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