// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import static jodd.servlet.UrlEncoder.appendUrl;

/**
 * Safe URL builder.
 */
public class UrlBuilder {

	protected final StringBuilder url;
	protected final String encoding;
	protected final HttpServletResponse response;
	protected boolean hasParams;

	public UrlBuilder(String baseUrl, String encoding) {
		this(baseUrl, encoding, null, null);
	}
	public UrlBuilder(String baseUrl, String encoding, HttpServletRequest request, HttpServletResponse response) {
		if (request != null) {
			baseUrl = ServletUtil.resolveUrl(baseUrl, request);
		}
		url = new StringBuilder(baseUrl);
		this.hasParams = baseUrl.indexOf('?') != -1;
		this.encoding = encoding;
		this.response = response;
	}

	/**
	 * Appends new parameter to url.
	 */
	public UrlBuilder param(String name, Object value) {
		return param(name, value == null ? null : value.toString());
	}

	/**
	 * Appends new parameter to url.
	 */
	public UrlBuilder param(String name, String value) {
		url.append(hasParams ? '&' : '?');
		hasParams = true;
		appendUrl(url, name, encoding);
		if ((value != null) && (value.length() > 0)) {
			url.append('=');
			appendUrl(url, value, encoding);
		}
		return this;
	}

	public UrlBuilder param(String nameValue) {
		url.append(hasParams ? '&' : '?');
		hasParams = true;
		int eqNdx = nameValue.indexOf('=');
		String name; String value = null;
		if (eqNdx == -1) {
			name = nameValue;
		} else {
			name = nameValue.substring(0, eqNdx);
			value = nameValue.substring(eqNdx + 1);
		}

		appendUrl(url, name, encoding);
		if ((value != null) && (value.length() > 0)) {
			url.append('=');
			appendUrl(url, value, encoding);
		}
		return this;
	}

	/**
	 * Returns builded url.
	 */
	@Override
	public String toString() {
		String result = url.toString();
		return response != null ? response.encodeURL(result) : result;
	}
}
