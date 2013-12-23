// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import javax.net.SocketFactory;

/**
 * <code>SocketFactory</code> for creating sockets that connect through the specified proxy.
 */
public class ProxySocketFactory extends SocketFactory {

	protected final Proxy proxy;

	public ProxySocketFactory() {
		this.proxy = Proxy.NO_PROXY;
	}

	/**
	 * Create all sockets with the specified proxy.
	 */
	public ProxySocketFactory(Proxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * A convenience constructor for creating a proxy with the specified host and port.
	 */
	public ProxySocketFactory(String hostname, int port) {
		this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostname, port));
	}

	@Override
	public Socket createSocket() throws IOException {
		return new Socket(proxy);
	}

	@Override
	public Socket createSocket(String hostname, int port) throws IOException {
		Socket socket = createSocket();
		socket.connect(new InetSocketAddress(hostname, port));
		return socket;
	}

	@Override
	public Socket createSocket(String hostname, int port, InetAddress localAddress, int localPort) throws IOException {
		Socket socket = createSocket();
		socket.bind(new InetSocketAddress(localAddress, localPort));
		socket.connect(new InetSocketAddress(hostname, port));
		return socket;
	}

	@Override
	public Socket createSocket(InetAddress address, int port) throws IOException {
		Socket socket = createSocket();
		socket.connect(new InetSocketAddress(address, port));
		return socket;
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		Socket socket = createSocket();
		socket.bind(new InetSocketAddress(localAddress, localPort));
		socket.connect(new InetSocketAddress(address, port));
		return socket;
	}

}