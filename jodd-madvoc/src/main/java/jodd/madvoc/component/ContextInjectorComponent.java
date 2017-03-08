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

import jodd.madvoc.ScopeData;
import jodd.madvoc.injector.Target;
import jodd.petite.PetiteContainer;
import jodd.petite.meta.PetiteInject;

import javax.servlet.ServletContext;

/**
 * Context injector for all singleton Madvoc elements, like results and interceptors.
 */
public class ContextInjectorComponent {

	@PetiteInject
	protected PetiteContainer madpc;

	@PetiteInject
	protected InjectorsManager injectorsManager;

	@PetiteInject
	protected MadvocController madvocController;

	@PetiteInject
	protected ScopeDataResolver scopeDataResolver;

	/**
	 * Inject context into target.
	 */
	public void injectContext(Target target) {
		Class targetType = target.resolveType();

		ScopeData[] scopeData = scopeDataResolver.resolveScopeData(targetType);

		ServletContext servletContext = madvocController.getApplicationContext();

		injectorsManager.getMadvocContextScopeInjector().injectContext(target, scopeData, madpc);
		injectorsManager.getMadvocParamsInjector().injectContext(target, scopeData, madpc);

		injectorsManager.getServletContextScopeInjector().injectContext(target, scopeData, servletContext);
		injectorsManager.getApplicationScopeInjector().injectContext(target, scopeData, servletContext);
	}

}