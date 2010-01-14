// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import javax.servlet.ServletRequestListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

/**
 * Store request in the current thread and all childs threads, so it can be easily accessible.
 */
public class RequestContextListener implements ServletRequestListener {

	private static final ThreadLocal<HttpServletRequest> requestHolder = new InheritableThreadLocal<HttpServletRequest>();

	public void requestInitialized(ServletRequestEvent requestEvent) {
		if ((requestEvent.getServletRequest() instanceof HttpServletRequest) == false) {
			throw new IllegalArgumentException("Request is not an HttpServletRequest: " + requestEvent.getServletRequest());
		}
		HttpServletRequest request = (HttpServletRequest) requestEvent.getServletRequest();
		requestHolder.set(request);
	}

	public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
		requestHolder.set(null);
	}

	/**
	 * Returns current http servlet request.
	 */
	public static HttpServletRequest getRequest() {
		return requestHolder.get();
	}
}