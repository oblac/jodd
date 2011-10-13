// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.decora;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Special <code>HttpServletRequestWrapper</code> allows filtering of the HTTP headers.
 */
public class DecoraRequestWrapper extends HttpServletRequestWrapper {

	public DecoraRequestWrapper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	/**
	 * Returns <code>null</code> for excluded HTTP headers.
	 */
	@Override
	public String getHeader(String header) {
		if (isExcluded(header)) {
			return null;
		} else {
			return super.getHeader(header);
		}
	}

	/**
	 * Returns <code>-1</code> for excluded HTTP headers.
	 */
	@Override
	public long getDateHeader(String header) {
		if (isExcluded(header)) {
			return -1;
		} else {
			return super.getDateHeader(header);
		}
	}

	/**
	 * Checks if header name is exclused.
	 */
	protected boolean isExcluded(String header) {
		return "If-Modified-Since".equalsIgnoreCase(header);
	}

}

