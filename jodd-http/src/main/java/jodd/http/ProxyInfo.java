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

package jodd.http;

/**
 * Proxy information.
 */
public class ProxyInfo {

	/**
	 * Proxy types.
	 */
	public enum ProxyType {
		NONE, HTTP, SOCKS4, SOCKS5
	}

	private final String proxyAddress;
	private final int proxyPort;
	private final String proxyUsername;
	private final String proxyPassword;
	private final ProxyType proxyType;

	public ProxyInfo(final ProxyType proxyType, final String proxyHost, final int proxyPort, final String proxyUser, final String proxyPassword) {
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
	public static ProxyInfo socks4Proxy(final String proxyAddress, final int proxyPort, final String proxyUser) {
		return new ProxyInfo(ProxyType.SOCKS4, proxyAddress, proxyPort, proxyUser, null);
	}

	/**
	 * Creates SOCKS5 proxy.
	 */
	public static ProxyInfo socks5Proxy(final String proxyAddress, final int proxyPort, final String proxyUser, final String proxyPassword) {
		return new ProxyInfo(ProxyType.SOCKS5, proxyAddress, proxyPort, proxyUser, proxyPassword);
	}

	/**
	 * Creates HTTP proxy.
	 */
	public static ProxyInfo httpProxy(final String proxyAddress, final int proxyPort, final String proxyUser, final String proxyPassword) {
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

}