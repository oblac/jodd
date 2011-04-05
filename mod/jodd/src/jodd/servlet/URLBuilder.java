// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import static jodd.servlet.URLCoder.appendPath;
import static jodd.servlet.URLCoder.appendQuery;

/**
 * Safe URL builder.
 */
public class URLBuilder {

	protected final StringBuilder url;
	protected final String encoding;
	protected final HttpServletRequest request;
	protected final HttpServletResponse response;
	protected boolean hasParams;
	protected boolean firstpath;

	public URLBuilder(HttpServletRequest request, HttpServletResponse response, String encoding) {
		url = new StringBuilder();
		this.hasParams = false;
		this.encoding = encoding;
		this.request = request;
		this.response = response;
		this.firstpath = true;
	}

	/**
	 * Defines path.
	 */
	public URLBuilder path(String value) {
		if (hasParams) {
			throw new IllegalArgumentException("Path element can't come after query parameters.");
		}
		if (firstpath == true) {
			if (request != null) {
				value = ServletUtil.resolveUrl(value, request);
			}
			firstpath = false;
		}
		appendPath(url, value);
		return this;
	}

	/**
	 * Appends new parameter to url.
	 */
	public URLBuilder param(String name, Object value) {
		return param(name, value == null ? null : value.toString());
	}

	/**
	 * Appends new parameter to url.
	 */
	public URLBuilder param(String name, String value) {
		url.append(hasParams ? '&' : '?');
		hasParams = true;
		appendQuery(url, name, encoding);
		if ((value != null) && (value.length() > 0)) {
			url.append('=');
			appendQuery(url, value, encoding);
		}
		return this;
	}

	public URLBuilder param(String nameValue) {
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

		appendQuery(url, name, encoding);
		if ((value != null) && (value.length() > 0)) {
			url.append('=');
			appendQuery(url, value, encoding);
		}
		return this;
	}

	/**
	 * Returns built URL.
	 */
	@Override
	public String toString() {
		String result = url.toString();
		return response != null ? response.encodeURL(result) : result;
	}
}
