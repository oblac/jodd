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

package jodd.madvoc.action;

import jodd.madvoc.MadvocConfig;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.FiltersManager;
import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Out;
import jodd.madvoc.meta.Scope;
import jodd.madvoc.result.ActionResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static jodd.madvoc.ScopeType.CONTEXT;

/**
 * Helper action that returns sorted list of all registered action runtime configurations,
 * action results and interceptors. It can be extended as an Madvoc action or used independently.
 */
public class ListMadvocConfig {

	@In @Scope(CONTEXT)
	protected MadvocConfig madvocConfig;

	@In @Scope(CONTEXT)
	protected ActionsManager actionsManager;

	@In @Scope(CONTEXT)
	protected FiltersManager filtersManager;

	@In @Scope(CONTEXT)
	protected InterceptorsManager interceptorsManager;

	@In @Scope(CONTEXT)
	protected ResultsManager resultsManager;

	@Out
	protected List<ActionRuntime> actions;

	@Out
	protected List<ActionResult> results;

	@Out
	protected List<ActionInterceptor> interceptors;

	@Out
	protected List<ActionFilter> filters;

	/**
	 * Collects all interceptors.
	 */
	protected void collectActionInterceptors() {
		Collection<? extends ActionInterceptor> interceptorValues = interceptorsManager.getAllInterceptors();
		interceptors = new ArrayList<>();
		interceptors.addAll(interceptorValues);
		interceptors.sort(Comparator.comparing(a -> a.getClass().getSimpleName()));
	}

	/**
	 * Collects all filters.
	 */
	protected void collectActionFilters() {
		Collection<? extends ActionFilter> filterValues = filtersManager.getAllFilters();
		filters = new ArrayList<>();
		filters.addAll(filterValues);
		filters.sort(Comparator.comparing(a -> a.getClass().getSimpleName()));
	}

	/**
	 * Collects all action results.
	 */
	protected void collectActionResults() {
		Collection<ActionResult> resultsValues = resultsManager.getAllActionResults();
		results = new ArrayList<>();
		results.addAll(resultsValues);
		results.sort(Comparator.comparing(a -> a.getClass().getSimpleName()));
	}

	/**
	 * Collects all action runtime configurations.
	 */
	protected void collectActionRuntimes() {
		actions = actionsManager.getAllActionRuntimes();
		actions.sort(Comparator.comparing(ActionRuntime::actionPath));
	}

}