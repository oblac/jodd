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

package jodd.madvoc.injector;

import jodd.bean.BeanUtil;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeData;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ScopeDataResolver;
import jodd.servlet.CsrfShield;
import jodd.servlet.map.HttpServletContextMap;
import jodd.servlet.map.HttpServletRequestMap;
import jodd.servlet.map.HttpServletRequestParamMap;
import jodd.servlet.map.HttpSessionMap;
import jodd.servlet.ServletUtil;
import jodd.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Injects values from various Servlet contexts.
 * It may inject:
 * <ul>
 * <li>raw servlet objects (request, session...)</li>
 * <li>map adapters</li>
 * <li>various values from servlet objects</li>
 * <li>cookies</li>
 * </ul>
 */
public class ServletContextScopeInjector extends BaseScopeInjector
		implements Injector, Outjector, ContextInjector<ServletContext> {

	public static final String REQUEST_NAME = "request";
	public static final String SESSION_NAME = "session";
	public static final String CONTEXT_NAME = "context";
	public static final String REQUEST_MAP = "requestMap";
	public static final String REQUEST_PARAM_MAP = "requestParamMap";
	public static final String REQUEST_BODY = "requestBody";
	public static final String SESSION_MAP = "sessionMap";
	public static final String CONTEXT_MAP = "contextMap";

	public static final String COOKIE_NAME = "cookie";

	public static final String CSRF_NAME = "csrfTokenValid";

	public ServletContextScopeInjector(ScopeDataResolver scopeDataResolver) {
		super(ScopeType.SERVLET, scopeDataResolver);
		silent = true;
	}

	/**
	 * Injects servlet context scope data.
	 */
	@SuppressWarnings({"ConstantConditions"})
	public void inject(ActionRequest actionRequest) {
		ScopeData[] injectData = lookupScopeData(actionRequest);
		if (injectData == null) {
			return;
		}

		Target[] targets = actionRequest.getTargets();

		HttpServletRequest servletRequest = actionRequest.getHttpServletRequest();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();

		for (int i = 0; i < targets.length; i++) {
			Target target = targets[i];
			if (injectData[i] == null) {
				continue;
			}
			ScopeData.In[] scopes = injectData[i].in;
			if (scopes == null) {
				continue;
			}

			for (ScopeData.In in : scopes) {
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
				} else if (in.name.equals(REQUEST_PARAM_MAP)) {
					value = new HttpServletRequestParamMap(servletRequest);
				} else if (in.name.equals(REQUEST_BODY)) {
					try {
						value = ServletUtil.readRequestBody(servletRequest);
					} catch (IOException e) {
						value = e.toString();
					}
				} else if (in.name.equals(REQUEST_BODY)) {
					value = new HttpServletRequestParamMap(servletRequest);
				} else if (in.name.equals(SESSION_MAP)) {
					value = new HttpSessionMap(servletRequest);
				} else if (in.name.equals(CONTEXT_MAP)) {
					value = new HttpServletContextMap(servletRequest);
				} else

				// names partial
				if (in.name.startsWith(REQUEST_NAME)) {
					value = BeanUtil.declared.getProperty(servletRequest, StringUtil.uncapitalize(in.name.substring(REQUEST_NAME.length())));
				} else if (in.name.startsWith(SESSION_NAME)) {
					value = BeanUtil.declared.getProperty(servletRequest.getSession(), StringUtil.uncapitalize(in.name.substring(SESSION_NAME.length())));
				} else if (in.name.startsWith(CONTEXT_NAME)) {
					value = BeanUtil.declared.getProperty(servletRequest.getSession().getServletContext(), StringUtil.uncapitalize(in.name.substring(CONTEXT_NAME.length())));
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

					setTargetProperty(target, property, value);
				}
			}
		}
	}

	/**
	 * Injects just context.
	 */
	public void injectContext(Target target, ScopeData[] scopeData, ServletContext servletContext) {
		ScopeData.In[] injectData = lookupInData(scopeData);
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
				value = BeanUtil.declared.getProperty(servletContext, StringUtil.uncapitalize(in.name.substring(CONTEXT_NAME.length())));
			}

			if (value != null) {
				String property = in.target != null ? in.target : in.name;

				setTargetProperty(target, property, value);
			}
		}
	}

	public void outject(ActionRequest actionRequest) {
		ScopeData[] outjectData = lookupScopeData(actionRequest);
		if (outjectData == null) {
			return;
		}

		Target[] targets = actionRequest.getTargets();
		HttpServletResponse servletResponse = actionRequest.getHttpServletResponse();

		for (int i = 0; i < targets.length; i++) {
			Target target = targets[i];
			if (outjectData[i] == null) {
				continue;
			}
			ScopeData.Out[] scopes = outjectData[i].out;
			if (scopes == null) {
				continue;
			}

			for (ScopeData.Out out : scopes) {
				if (out.name.startsWith(COOKIE_NAME)) {

					Cookie cookie = (Cookie) getTargetProperty(target, out);
					if (cookie != null) {
						servletResponse.addCookie(cookie);
					}
				}
			}
		}
	}
}