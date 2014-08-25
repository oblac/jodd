// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Emulates HTTP Browser and persist cookies between requests.
 */
public class HttpBrowser {

	protected HttpConnectionProvider httpConnectionProvider;
	protected HttpRequest httpRequest;
	protected HttpResponse httpResponse;
	protected Map<String, Cookie> cookies = new LinkedHashMap<String, Cookie>();
	protected boolean keepAlive;
	protected long elapsedTime;

	public HttpBrowser() {
		httpConnectionProvider = JoddHttp.httpConnectionProvider;
	}

	/**
	 * Returns <code>true</code> if keep alive is used.
	 */
	public boolean isKeepAlive() {
		return keepAlive;
	}

	/**
	 * Defines that persistent HTTP connection should be used.
	 */
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	/**
	 * Defines proxy for a browser.
	 */
	public void setProxyInfo(ProxyInfo proxyInfo) {
		httpConnectionProvider.useProxy(proxyInfo);
	}

	/**
	 * Defines {@link jodd.http.HttpConnectionProvider} for this browser session.
	 * Resets the previous proxy definition, if set.
	 */
	public void setHttpConnectionProvider(HttpConnectionProvider httpConnectionProvider) {
		this.httpConnectionProvider = httpConnectionProvider;
	}

	/**
	 * Returns last used request.
	 */
	public HttpRequest getHttpRequest() {
		return httpRequest;
	}

	/**
	 * Returns last received {@link HttpResponse HTTP response} object.
	 */
	public HttpResponse getHttpResponse() {
		return httpResponse;
	}

	/**
	 * Returns last response HTML page.
	 */
	public String getPage() {
		if (httpResponse == null) {
			return null;
		}
		return httpResponse.bodyText();
	}


	/**
	 * Sends new request as a browser. Before sending,
	 * all browser cookies are added to the request.
	 * After sending, the cookies are read from the response.
	 * Moreover, status codes 301 and 302 are automatically
	 * handled. Returns very last response.
	 */
	public HttpResponse sendRequest(HttpRequest httpRequest) {
		elapsedTime = System.currentTimeMillis();

		// send request

		while (true) {
			this.httpRequest = httpRequest;
			HttpResponse previouseResponse = this.httpResponse;
			this.httpResponse = null;

			addCookies(httpRequest);

			// send request
			if (keepAlive == false) {
				httpRequest.open(httpConnectionProvider);
			} else {
				// keeping alive
				if (previouseResponse == null) {
					httpRequest.open(httpConnectionProvider).connectionKeepAlive(true);
				} else {
					httpRequest.keepAlive(previouseResponse, true);
				}
			}

			this.httpResponse = httpRequest.send();

			readCookies(httpResponse);

			int statusCode = httpResponse.statusCode();

			// 301: moved permanently
			if (statusCode == 301) {
				String newPath = httpResponse.header("location");

				httpRequest = HttpRequest.get(newPath);
				continue;
			}

			// 302: redirect, 303: see other
			if (statusCode == 302 || statusCode == 303) {
				String newPath = httpResponse.header("location");

				httpRequest = HttpRequest.get(newPath);
				continue;
			}

			// 307: temporary redirect
			if (statusCode == 307) {
				String newPath = httpResponse.header("location");

				String originalMethod = httpRequest.method();
				httpRequest = new HttpRequest()
						.method(originalMethod)
						.set(newPath);
				continue;
			}

			break;
		}

		elapsedTime = System.currentTimeMillis() - elapsedTime;

		return this.httpResponse;
	}

	/**
	 * Returns elapsed time of last {@link #sendRequest(HttpRequest)} in milliseconds.
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	// ---------------------------------------------------------------- close

	/**
	 * Closes browser explicitly, needed when keep-alive connection is used.
	 */
	public void close() {
		if (httpResponse != null) {
			httpResponse.close();
		}
	}

	// ---------------------------------------------------------------- cookies

	/**
	 * Reads cookies from response.
	 */
	protected void readCookies(HttpResponse httpResponse) {
		String[] newCookies = httpResponse.headers("set-cookie");

		if (newCookies != null) {
			for (String cookieValue : newCookies) {
				Cookie cookie = new Cookie(cookieValue);

				cookies.put(cookie.getName(), cookie);
			}
		}
	}

	/**
	 * Add cookies to the request.
	 */
	protected void addCookies(HttpRequest httpRequest) {
		// prepare all cookies

		StringBuilder cookieString = new StringBuilder();
		boolean first = true;

		if (!cookies.isEmpty()) {
			for (Cookie cookie: cookies.values()) {
				if (!first) {
					cookieString.append("; ");
				}
				first = false;
				cookieString.append(cookie.getName());
				cookieString.append('=');
				cookieString.append(cookie.getValue());
			}

			httpRequest.header("cookie", cookieString.toString(), true);
		}
	}
}