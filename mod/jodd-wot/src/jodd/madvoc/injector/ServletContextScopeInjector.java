// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.ScopeType;
import jodd.servlet.CsrfShield;
import jodd.servlet.HttpServletContextMap;
import jodd.servlet.HttpServletRequestMap;
import jodd.servlet.HttpSessionMap;
import jodd.servlet.ServletUtil;
import jodd.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Injects values from various Servlet contexts.
 * It may inject:
 * <p>raw servlet objects (request, session...)
 * <p>map adapters
 * <p>various values from servlet objects
 * <p>cookies
 */
public class ServletContextScopeInjector extends BaseScopeInjector {

	public static final String REQUEST_NAME = "request";
	public static final String SESSION_NAME = "session";
	public static final String CONTEXT_NAME = "context";
	public static final String REQUEST_MAP = "requestMap";
	public static final String SESSION_MAP = "sessionMap";
	public static final String CONTEXT_MAP = "contextMap";

	public static final String COOKIE_NAME = "cookie";

	public static final String CSRF_NAME = "csrfTokenValid";

	public ServletContextScopeInjector() {
		super(ScopeType.SERVLET);
	}

	@SuppressWarnings({"ConstantConditions"})
	public void inject(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		ScopeData.In[] injectData = lookupInData(target.getClass());
		if (injectData == null) {
			return;
		}
		for (ScopeData.In in : injectData) {
			Class fieldType = in.type;
			Object value = null;

			// raw servlet types
			if (fieldType.equals(HttpServletRequest.class)) {			// correct would be: ReflectUtil.isSubclass()
				value = servletRequest;
			} else if (fieldType.equals(HttpServletResponse.class)) {
				value = servletResponse;
			} else if (fieldType.equals(HttpSession.class)) {
				value = servletRequest.getSession();
			} else if (fieldType.equals(ServletContext.class)) {
				value = servletRequest.getSession().getServletContext();
			} else

			// names
			if (in.name.equals(REQUEST_MAP)) {
				value = new HttpServletRequestMap(servletRequest);
			} else if (in.name.equals(SESSION_MAP)) {
				value = new HttpSessionMap(servletRequest);
			} else if (in.name.equals(CONTEXT_MAP)) {
				value = new HttpServletContextMap(servletRequest);
			} else

			// names partial
			if (in.name.startsWith(REQUEST_NAME)) {
				value = BeanUtil.getDeclaredProperty(servletRequest, StringUtil.uncapitalize(in.name.substring(REQUEST_NAME.length())));
			} else if (in.name.startsWith(SESSION_NAME)) {
				value = BeanUtil.getDeclaredProperty(servletRequest.getSession(), StringUtil.uncapitalize(in.name.substring(SESSION_NAME.length())));
			} else if (in.name.startsWith(CONTEXT_NAME)) {
				value = BeanUtil.getDeclaredProperty(servletRequest.getSession().getServletContext(), StringUtil.uncapitalize(in.name.substring(CONTEXT_NAME.length())));
			} else

			// csrf
			if (in.name.equals(CSRF_NAME)) {
				value = Boolean.valueOf(CsrfShield.checkCsrfToken(servletRequest));
			}

			// cookies
			if (in.name.startsWith(COOKIE_NAME)) {
				String cookieName = StringUtil.uncapitalize(in.name.substring(COOKIE_NAME.length()));
				if (fieldType.isArray()) {
					if (fieldType.getComponentType().equals(Cookie.class)) {
						if (StringUtil.isEmpty(cookieName)) {
							value = servletRequest.getCookies();		// get all cookies
						} else {
							value = ServletUtil.getAllCookies(servletRequest, cookieName);	// get all cookies by name
						}
					}
				} else {
					value = ServletUtil.getCookie(servletRequest, cookieName);	// get single cookie
				}
			}

			if (value != null) {
				String property = in.target != null ? in.target : in.name;
				BeanUtil.setDeclaredProperty(target, property, value);
			}
		}
	}

	public void inject(Object target, ServletContext servletContext) {
		ScopeData.In[] injectData = lookupInData(target.getClass());
		if (injectData == null) {
			return;
		}

		for (ScopeData.In in : injectData) {
			Class fieldType = in.type;
			Object value = null;

			if (fieldType.equals(ServletContext.class)) {
				// raw servlet type
				value = servletContext;
			} else if (in.name.equals(CONTEXT_MAP)) {
				// names
				value = new HttpServletContextMap(servletContext);
			} else if (in.name.startsWith(CONTEXT_NAME)) {
				value = BeanUtil.getDeclaredProperty(servletContext, StringUtil.uncapitalize(in.name.substring(CONTEXT_NAME.length())));
			}

			if (value != null) {
				String property = in.target != null ? in.target : in.name;
				BeanUtil.setDeclaredProperty(target, property, value);
			}
		}
	}


	public void outject(Object target, HttpServletResponse servletResponse) {
		ScopeData.Out[] outjectData = lookupOutData(target.getClass());
		if (outjectData == null) {
			return;
		}

		for (ScopeData.Out out : outjectData) {
			if (out.name.startsWith(COOKIE_NAME)) {
				Cookie cookie = (Cookie) BeanUtil.getDeclaredProperty(target, out.name);
				if (cookie != null) {
					servletResponse.addCookie(cookie);
				}
			}
		}



	}
}