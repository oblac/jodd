// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;
import jodd.madvoc.MadvocException;
import jodd.bean.BeanUtil;
import jodd.petite.PetiteContainer;
import jodd.servlet.HttpServletRequestMap;
import jodd.servlet.HttpSessionMap;
import jodd.servlet.HttpServletContextMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

/**
 * Madvoc context injector.
 */
public class MadvocContextScopeInjector extends BaseScopeInjector {

	private static final String REQUEST_MAP = "requestMap";
	private static final String SESSION_MAP = "sessionMap";
	private static final String CONTEXT_MAP = "contextMap";

	protected final PetiteContainer madpc;

	public MadvocContextScopeInjector(PetiteContainer madpc) {
		super(ScopeType.CONTEXT);
		this.madpc = madpc;
	}

	@SuppressWarnings({"ConstantConditions"})
	public void inject(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		ScopeData.In[] injectData = lookupInData(target.getClass());
		if (injectData == null) {
			return;
		}
		for (ScopeData.In in : injectData) {
			Class fieldType = in.type;
			Object value;

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
			} else {
				value = madpc.getBean(in.name);
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
			Object value;

			// raw servlet types
			if (fieldType.equals(ServletContext.class)) {
				value = servletContext;
			} else
			// names
			if (in.name.equals(CONTEXT_MAP)) {
				value = new HttpServletContextMap(servletContext);
			} else {
				value = madpc.getBean(in.name);
			}
			if (value != null) {
				String property = in.target != null ? in.target : in.name;
				BeanUtil.setDeclaredProperty(target, property, value);
			}
		}
	}


	@SuppressWarnings({"UnusedDeclaration"})
	public void outject(Object target, HttpServletRequest servletRequest) {
		ScopeData.Out[] outjectData = lookupOutData(target.getClass());
		if (outjectData == null) {
			return;
		}
		throw new MadvocException("Madvoc context can't be outjected.");
	}
}