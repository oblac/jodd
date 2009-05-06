// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import jodd.util.StringUtil;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Replacement and enhancement of <code>HttpServlet</code>, containing additional
 * functionalities.
 * <p>
 *
 * Here both POST and GET requests calls <code>doRequest</code> method, which
 * is the entering point of the servlet. For more easier work, this servlet
 * may use one of the helper methods. Helper methods can be grouped in 3
 * groups:
 *
 * <ul>
 *
 * <li>invokers - they execute an action. Action may be specified in request
 * parameter 'action' or internal, during method invocation. Actions are
 * (names of the) methods in the same <code>ServletAction</code>
 * implementation. Action methods can help in reducing the number of
 * <code>ServletAction</code> classes, since one class may be used for
 * handling more than one request.</li>
 *
 * <li>forwarders - perform forwards to specific URL. They also can read
 * forward destination from the request parameter 'forward'. </ul>
 *
 * <li>redirectors - perform redirect to specific URL. They also can read
 * redirect destination from the request parameter 'redirect'. </ul>
 * </ul>
 *
 * Default forwarders and redirector names given as request parameter are
 * either 'forward' or 'redirect' respectively. If more than one parameter
 * is needed, they have to be named as 'forward-...' or 'redirect-...'.
 */
public abstract class ServletAction extends HttpServlet {

	// ---------------------------------------------------------------- HTTP method

	public static final int HTTP_METHOD_UNKNOWN = 0;
	public static final int HTTP_METHOD_GET = 1;
	public static final int HTTP_METHOD_POST = 2;

	private int method = HTTP_METHOD_UNKNOWN;


	/**
	 * Returns HTTP method (POST/GET) which invoked the action.
	 */
	public int getHttpMethod() {
		return method;
	}

	/**
	 * Default doGet method, calls {@link #doRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		method = HTTP_METHOD_GET;
		doRequest(request, response);
	}

	/**
	 * Default doPost method, calls {@link #doRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		method = HTTP_METHOD_POST;
		doRequest(request, response);
	}

	/**
	 * Main get/post request handler.
	 */
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	}

	// ---------------------------------------------------------------- UTILTIES: forward

	/**
	 * Performs forward with use of the RequestDispatcher.
	 */
	public boolean forward(HttpServletRequest request, HttpServletResponse response, String url) throws IOException, ServletException {
		return DispatcherUtil.forward(request, response, url);
	}

	/**
	 * Performs forward with use of the RequestDispatcher.
	 * URL is read from the request. If URL doesn't exist, nothing happens.
	 *
	 * @return true if parameter found, false otherwise
	 */
	public boolean forwardParam(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String url = request.getParameter("forward");
		if (url != null) {
			return forward(request, response, url);
		}
		return false;
	}

	/**
	 * Performs forward where URL is read from the request. If URL doesn't exist,
	 * nothing happens.
	 *
	 * @return <code>true</code> if parameter found and forward was successful,
	 *         <code>false</code> otherwise
	 */
	public boolean forwardParam(HttpServletRequest request, HttpServletResponse response, String s) throws IOException, ServletException {
		String url = request.getParameter("forward-" + s);
		if (url != null) {
			return forward(request, response, url);
		}
		return false;

	}


	// ---------------------------------------------------------------- UTILTIES: redirect

	/**
	 * Performs redirection.
	 */
	protected static void redirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		DispatcherUtil.redirect(request, response, url);
	}

	/**
	 * Performs redirection. URL is read from the request. If URL doesn't exist,
	 * nothing happens.
	 *
	 * @return true if parameter found, false otherwise
	 */
	protected static boolean redirectParam(HttpServletRequest request, HttpServletResponse response)  throws IOException {
		String url = request.getParameter("redirect");
		if (url != null) {
			redirect(request, response, url);
			return true;
		}
		return false;

	}

	/**
	 * Performs redirection where URL is read from the request. If URL doesn't
	 * exist, nothing happens.
	 *
	 * @return true if parameter found, false otherwise
	 */
	protected static boolean redirectParam(HttpServletRequest request, HttpServletResponse response, String s) throws IOException {
		String url = request.getParameter("redirect-" + s);
		if (url != null) {
			redirect(request, response, url);
			return true;
		}
		return false;

	}




	// ---------------------------------------------------------------- UTILTIES: invokers

	/**
	 * Calls a method defined in the request parameter. Method name is read from
	 * the request. If method name is null (doesn't exist in the request) nothing
	 * happens.
	 *
	 * @return true if action was invoked, otherwise false
	 */
	public String invokeActionParam(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String actionName = request.getParameter("action");
		return invokeActionParam(this, request, response, actionName);
	}

	/**
	 * Invoke a method from this ServletAction class. Methods name is given as
	 * parameter. If method name is null nothing happens.
	 *
	 * @return null if error, otherwise string mapping
	 */
	public String invokeActionParam(Object servlet, HttpServletRequest request, HttpServletResponse response, String actionName) throws ServletException {
		if (actionName == null) {
			return null;
		}
		try {
			Method m = servlet.getClass().getMethod(actionName, HttpServletRequest.class, HttpServletResponse.class);
			Object result = m.invoke(servlet, request, response);
			return StringUtil.toString(result);
		} catch (Exception ex) {
			throw new ServletException("Invocation error of action '" + actionName + "': " + ex.getMessage(), ex);
		}
	}


	// ---------------------------------------------------------------- UTILITIES: include

	/**
	 * Performs include page.
	 */
	public static boolean include(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
		return DispatcherUtil.include(request, response, page);
	}

	public static boolean includeParam(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String url = request.getParameter("include");
		if (url != null) {
			return include(request, response, url);
		}
		return false;
	}

	public static boolean includeParam(HttpServletRequest request, HttpServletResponse response, String includeName) throws IOException, ServletException {
		String url = request.getParameter("include-" + includeName);
		if (url != null) {
			return include(request, response, url);
		}
		return false;
	}


}
