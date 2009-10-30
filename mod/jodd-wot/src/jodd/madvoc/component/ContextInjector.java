// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.petite.meta.PetiteInject;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.PetiteContainer;
import jodd.madvoc.injector.ApplicationScopeInjector;
import jodd.madvoc.injector.MadvocContextScopeInjector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

public class ContextInjector {

	@PetiteInject
	protected PetiteContainer madpc;

	protected ApplicationScopeInjector applicationScopeInjector;
	protected MadvocContextScopeInjector madvocContextScopeInjector;

	@PetiteInitMethod(order = 1, firstOff = true)
	void createInjectors() {
		applicationScopeInjector = new ApplicationScopeInjector();
		madvocContextScopeInjector = new MadvocContextScopeInjector(madpc);
	}

	public ApplicationScopeInjector getApplicationScopeInjector() {
		return applicationScopeInjector;
	}

	public MadvocContextScopeInjector getMadvocContextScopeInjector() {
		return madvocContextScopeInjector;
	}

	/**
	 * Performs default context injection.
	 */
	public void injectContext(Object target, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		madvocContextScopeInjector.inject(target, servletRequest, servletResponse);
		applicationScopeInjector.inject(target, servletRequest.getSession().getServletContext());
	}

	/**
	 * Performs context injection when only servlet context is availiable. Should be called for all
	 * global instances (such as interceptors).
	 */
	public void injectContext(Object target, ServletContext servletContext) {
		madvocContextScopeInjector.inject(target, servletContext);
		applicationScopeInjector.inject(target, servletContext);
	}

}
