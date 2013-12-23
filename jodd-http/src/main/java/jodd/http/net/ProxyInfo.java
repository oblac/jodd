// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http.net;

import javax.net.SocketFactory;

/**
 * Proxy information.
 */
public class ProxyInfo {

	/**
	 * Proxy types.
	 */
	public static enum ProxyType {
		NONE, HTTP, SOCKS4, SOCKS5
	}

	private String proxyAddress;
	private int proxyPort;
	private String proxyUsername;
	private String proxyPassword;
	private ProxyType proxyType;

	public ProxyInfo(ProxyType proxyType, String proxyHost, int proxyPort, String proxyUser, String proxyPassword) {
		this.proxyType = proxyType;
		this.proxyAddress = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUsername = proxyUser;
		this.proxyPassword = proxyPassword;
	}

	// ---------------------------------------------------------------- factory

	/**
	 * Creates directProxy.
	 */
	public static ProxyInfo directProxy() {
		return new ProxyInfo(ProxyType.NONE, null, 0, null, null);
	}

	/**
	 * Creates SOCKS4 proxy.
	 */
	public static ProxyInfo socks4Proxy(String proxyAddress, int proxyPort, String proxyUser) {
		return new ProxyInfo(ProxyType.SOCKS4, proxyAddress, proxyPort, proxyUser, null);
	}

	/**
	 * Creates SOCKS5 proxy.
	 */
	public static ProxyInfo socks5Proxy(String proxyAddress, int proxyPort, String proxyUser, String proxyPassword) {
		return new ProxyInfo(ProxyType.SOCKS5, proxyAddress, proxyPort, proxyUser, proxyPassword);
	}

	/**
	 * Creates HTTP proxy.
	 */
	public static ProxyInfo httpProxy(String proxyAddress, int proxyPort, String proxyUser, String proxyPassword) {
		return new ProxyInfo(ProxyType.HTTP, proxyAddress, proxyPort, proxyUser, proxyPassword);
	}

	// ---------------------------------------------------------------- getter

	/**
	 * Returns proxy type.
	 */
	public ProxyType getProxyType() {
		return proxyType;
	}

	/**
	 * Returns proxy address.
	 */
	public String getProxyAddress() {
		return proxyAddress;
	}

	/**
	 * Returns proxy port.
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * Returns proxy user name or <code>null</code> if
	 * no authentication required.
	 */
	public String getProxyUsername() {
		return proxyUsername;
	}

	/**
	 * Returns proxy password or <code>null</code>.
	 */
	public String getProxyPassword() {
		return proxyPassword;
	}

	/**
	 * Returns socket factory based on socket
	 */
	public SocketFactory getSocketFactory() {
		switch (proxyType) {
			case NONE:
				return SocketFactory.getDefault();
			case HTTP:
				return new HTTPProxySocketFactory(this);
			case SOCKS4:
				return new Socks4ProxySocketFactory(this);
			case SOCKS5:
				return new Socks5ProxySocketFactory(this);
		}
		return null;
	}
}