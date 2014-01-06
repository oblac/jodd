// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.http;

import jodd.JoddHttp;

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

	public HttpBrowser() {
		httpConnectionProvider = JoddHttp.httpConnectionProvider;
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
	 * handled.
	 */
	public void sendRequest(HttpRequest httpRequest) {
		// send request

		while (true) {
			this.httpRequest = httpRequest;
			this.httpResponse = null;

			addCookies(httpRequest);

			// send request
			this.httpResponse = httpRequest.open(httpConnectionProvider).send();

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