// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import java.io.IOException;

/**
 * Factory for {@link HttpConnection http connections}.
 */
public interface HttpConnectionProvider {

	/**
	 * Specifies {@link ProxyInfo proxy} for provide to use.
	 */
	public void useProxy(ProxyInfo proxyInfo);


 	/**
	 * Creates new {@link HttpConnection}
	 * from {@link jodd.http.HttpRequest request}.
	 */
	public HttpConnection createHttpConnection(HttpRequest httpRequest) throws IOException;

}