// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.injector;

import jodd.madvoc.ScopeType;
import jodd.madvoc.MadvocException;
import jodd.madvoc.component.ScopeDataManager;
import jodd.bean.BeanUtil;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInject;
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
public class MadvocContextScopeInjector extends ScopeInjector {

	protected final PetiteContainer madpc;

	private static final String REQUEST_MAP = "requestMap";
	private static final String SESSION_MAP = "sessionMap";
	private static final String CONTEXT_MAP = "contextMap";

	@PetiteInject
	public MadvocContextScopeInjector(ScopeDataManager scopeDataManager, PetiteContainer madpc) {
		super(scopeDataManager);
		this.madpc = madpc;
	}

	@SuppressWarnings({"ConstantConditions"})
	public void inject(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		ScopeData.In[] injectData = scopeDataManager.lookupInData(target, ScopeType.CONTEXT);
		if (injectData == null) {
			return;
		}

		for (ScopeData.In ii : injectData) {
			Class fieldType = ii.type;
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
			if (ii.name.equals(REQUEST_MAP)) {
				value = new HttpServletRequestMap(servletRequest);
			} else if (ii.name.equals(SESSION_MAP)) {
				value = new HttpSessionMap(servletRequest);
			} else if (ii.name.equals(CONTEXT_MAP)) {
				value = new HttpServletContextMap(servletRequest);
			} else {
				value = madpc.getBean(ii.name);
			}
			if (value != null) {
				String property = ii.target != null ? ii.target : ii.name;
				BeanUtil.setDeclaredProperty(target, property, value);
			}
		}
	}

	public void inject(Object target, ServletContext servletContext) {
		ScopeData.In[] injectData = scopeDataManager.lookupInData(target, ScopeType.CONTEXT);
		if (injectData == null) {
			return;
		}

		for (ScopeData.In ii : injectData) {
			Class fieldType = ii.type;
			Object value;

			// raw servlet types
			if (fieldType.equals(ServletContext.class)) {
				value = servletContext;
			} else
			// names
			if (ii.name.equals(CONTEXT_MAP)) {
				value = new HttpServletContextMap(servletContext);
			} else {
				value = madpc.getBean(ii.name);
			}
			if (value != null) {
				String property = ii.target != null ? ii.target : ii.name;
				BeanUtil.setDeclaredProperty(target, property, value);
			}
		}
	}


	@SuppressWarnings({"UnusedDeclaration"})
	public void outject(Object target, HttpServletRequest servletRequest) {
		ScopeData.Out[] outjectData = scopeDataManager.lookupOutData(target, ScopeType.CONTEXT);
		if (outjectData == null) {
			return;
		}
		throw new MadvocException("Unable to outject to Madvoc context.");
	}
}