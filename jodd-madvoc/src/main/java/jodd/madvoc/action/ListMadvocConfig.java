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

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.FiltersManager;
import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.Out;
import jodd.madvoc.result.ActionResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Helper action that returns sorted list of all registered action configurations,
 * action results and interceptors. It can be extended as an Madvoc action or used independently.
 */
public class ListMadvocConfig {

	@In(scope = ScopeType.CONTEXT)
	@Out
	protected MadvocConfig madvocConfig;

	@In(scope = ScopeType.CONTEXT)
	protected ActionsManager actionsManager;

	@In(scope = ScopeType.CONTEXT)
	protected FiltersManager filtersManager;

	@In(scope = ScopeType.CONTEXT)
	protected InterceptorsManager interceptorsManager;

	@In(scope = ScopeType.CONTEXT)
	protected ResultsManager resultsManager;

	@Out
	protected List<ActionConfig> actions;

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
		Collections.sort(interceptors, new Comparator<ActionInterceptor>() {
			public int compare(ActionInterceptor a1, ActionInterceptor a2) {
				return a1.getClass().getSimpleName().compareTo(a2.getClass().getSimpleName());
			}
		});
	}

	/**
	 * Collects all filters.
	 */
	protected void collectActionFilters() {
		Collection<? extends ActionFilter> filterValues = filtersManager.getAllFilters();
		filters = new ArrayList<>();
		filters.addAll(filterValues);
		Collections.sort(filters, new Comparator<ActionFilter>() {
			public int compare(ActionFilter a1, ActionFilter a2) {
				return a1.getClass().getSimpleName().compareTo(a2.getClass().getSimpleName());
			}
		});
	}

	/**
	 * Collects all action results.
	 */
	protected void collectActionResults() {
		Collection<ActionResult> resultsValues = resultsManager.getAllActionResults();
		results = new ArrayList<>();
		results.addAll(resultsValues);
		Collections.sort(results, new Comparator<ActionResult>() {
			public int compare(ActionResult a1, ActionResult a2) {
				return a1.getClass().getSimpleName().compareTo(a2.getClass().getSimpleName());
			}
		});
	}

	/**
	 * Collects all action configurations.
	 */
	protected void collectActionConfigs() {
		actions = actionsManager.getAllActionConfigurations();
		Collections.sort(actions, new Comparator<ActionConfig>() {
			public int compare(ActionConfig a1, ActionConfig a2) {
				return a1.actionPath.compareTo(a2.actionPath);
			}
		});
	}

}