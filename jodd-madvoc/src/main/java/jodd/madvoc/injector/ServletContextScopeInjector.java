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
import jodd.madvoc.ScopeType;
import jodd.servlet.CsrfShield;
import jodd.servlet.ServletUtil;
import jodd.servlet.map.HttpServletContextMap;
import jodd.servlet.map.HttpServletRequestMap;
import jodd.servlet.map.HttpServletRequestParamMap;
import jodd.servlet.map.HttpSessionMap;
import jodd.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
public class ServletContextScopeInjector implements Injector, ContextInjector<ServletContext> {

	private final static ScopeType SCOPE_TYPE = ScopeType.SERVLET;

	public static final String REQUEST_NAME = "request";
	public static final String SESSION_NAME = "session";
	public static final String CONTEXT_NAME = "context";
	public static final String REQUEST_MAP = "requestMap";
	public static final String REQUEST_PARAM_MAP = "requestParamMap";
	public static final String REQUEST_BODY = "requestBody";
	public static final String SESSION_MAP = "sessionMap";
	public static final String CONTEXT_MAP = "contextMap";
	public static final String CSRF_NAME = "csrfTokenValid";

	/**
	 * Injects servlet context scope data.
	 */
	@Override
	@SuppressWarnings({"ConstantConditions"})
	public void inject(final ActionRequest actionRequest) {
		Targets targets = actionRequest.targets();
		if (!targets.usesScope(SCOPE_TYPE)) {
			return;
		}

		HttpServletRequest servletRequest = actionRequest.httpServletRequest();
		HttpServletResponse servletResponse = actionRequest.httpServletResponse();

		targets.forEachTargetAndInScopes(SCOPE_TYPE, (target, in) -> {
			final Class fieldType = in.type;
			Object value = null;

			// raw servlet types
			// todo measure ReflectUtil.isSubclass() vs .equals()
			if (fieldType.equals(HttpServletRequest.class)) {
				value = servletRequest;
			} else if (fieldType.equals(ServletRequest.class)) {
				value = servletRequest;
			} else if (fieldType.equals(HttpServletResponse.class)) {
				value = servletResponse;
			} else if (fieldType.equals(ServletResponse.class)) {
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
				String name = StringUtil.uncapitalize(in.name.substring(REQUEST_NAME.length()));
				if (!name.isEmpty()) {
					value = BeanUtil.declared.getProperty(servletRequest, name);
				}
			} else if (in.name.startsWith(SESSION_NAME)) {
				String name = StringUtil.uncapitalize(in.name.substring(SESSION_NAME.length()));
				if (!name.isEmpty()) {
					value = BeanUtil.declared.getProperty(servletRequest.getSession(), name);
				}
			} else if (in.name.startsWith(CONTEXT_NAME)) {
				String name = StringUtil.uncapitalize(in.name.substring(CONTEXT_NAME.length()));
				if (!name.isEmpty()) {
					value = BeanUtil.declared.getProperty(servletRequest.getSession().getServletContext(), name);
				}
			} else

			// csrf
			if (in.name.equals(CSRF_NAME)) {
				value = Boolean.valueOf(CsrfShield.checkCsrfToken(servletRequest));
			}

			if (value != null) {
				target.writeValue(in.propertyName(), value, true);
			}
		});
	}

	/**
	 * Injects just context.
	 */
	@Override
	public void injectContext(final Targets targets, final ServletContext servletContext) {
		targets.forEachTargetAndInScopes(SCOPE_TYPE, (target, in) -> {
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
				target.writeValue(in.propertyName(), value, true);
			}
		});
	}

}