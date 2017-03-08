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

package jodd.madvoc.component;

import jodd.madvoc.injector.ActionPathMacroInjector;
import jodd.madvoc.injector.ApplicationScopeInjector;
import jodd.madvoc.injector.MadvocContextScopeInjector;
import jodd.madvoc.injector.MadvocParamsInjector;
import jodd.madvoc.injector.RequestScopeInjector;
import jodd.madvoc.injector.ServletContextScopeInjector;
import jodd.madvoc.injector.SessionScopeInjector;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.meta.PetiteInject;

import static jodd.petite.meta.InitMethodInvocationStrategy.POST_DEFINE;

/**
 * Injectors manager creates and holds instances of all injectors.
 */
public class InjectorsManager {

	@PetiteInject
	protected PetiteContainer madpc;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ScopeDataResolver scopeDataResolver;

	protected RequestScopeInjector requestScopeInjector;
	protected SessionScopeInjector sessionScopeInjector;
	protected ActionPathMacroInjector actionPathMacroInjector;
	protected MadvocContextScopeInjector madvocContextScopeInjector;
	protected MadvocParamsInjector madvocParamsInjector;
	protected ApplicationScopeInjector applicationScopeInjector;
	protected ServletContextScopeInjector servletContextScopeInjector;

	@PetiteInitMethod(order = 1, invoke = POST_DEFINE)
	void createInjectors() {
		requestScopeInjector = new RequestScopeInjector(madvocConfig, scopeDataResolver);
		sessionScopeInjector = new SessionScopeInjector(scopeDataResolver);
		actionPathMacroInjector = new ActionPathMacroInjector(scopeDataResolver);
		madvocContextScopeInjector = new MadvocContextScopeInjector(scopeDataResolver, madpc);
		madvocParamsInjector = new MadvocParamsInjector(madvocConfig);
		applicationScopeInjector = new ApplicationScopeInjector(scopeDataResolver);
		servletContextScopeInjector = new ServletContextScopeInjector(scopeDataResolver);
	}

	// ---------------------------------------------------------------- getter

	public RequestScopeInjector getRequestScopeInjector() {
		return requestScopeInjector;
	}

	public SessionScopeInjector getSessionScopeInjector() {
		return sessionScopeInjector;
	}

	public ActionPathMacroInjector getActionPathMacroInjector() {
		return actionPathMacroInjector;
	}

	public MadvocContextScopeInjector getMadvocContextScopeInjector() {
		return madvocContextScopeInjector;
	}

	public MadvocParamsInjector getMadvocParamsInjector() {
		return madvocParamsInjector;
	}

	public ApplicationScopeInjector getApplicationScopeInjector() {
		return applicationScopeInjector;
	}

	public ServletContextScopeInjector getServletContextScopeInjector() {
		return servletContextScopeInjector;
	}
}