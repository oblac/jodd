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

package jodd.madvoc;

import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.path.ActionNamingStrategy;
import jodd.madvoc.result.ActionResult;

/**
 * Action configuration.
 */
public class ActionConfig {

	private Class<? extends ActionResult> actionResult;
	private Class<? extends ActionInterceptor>[] interceptors;
	private Class<? extends ActionFilter>[] filters;
	private String[] actionMethodNames;
	private Class<? extends ActionNamingStrategy> namingStrategy;

	public ActionConfig() {
	}

	public ActionConfig(final ActionConfig parentActionConfig) {
		this.actionResult = parentActionConfig.getActionResult();
		this.interceptors = parentActionConfig.getInterceptors();
		this.filters = parentActionConfig.getFilters();
		this.actionMethodNames = parentActionConfig.getActionMethodNames();
		this.namingStrategy = parentActionConfig.getNamingStrategy();
	}

	public Class<? extends ActionResult> getActionResult() {
		return actionResult;
	}

	public void setActionResult(final Class<? extends ActionResult> actionResult) {
		this.actionResult = actionResult;
	}

	public Class<? extends ActionInterceptor>[] getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(final Class<? extends ActionInterceptor>... interceptors) {
		this.interceptors = interceptors;
	}

	public Class<? extends ActionFilter>[] getFilters() {
		return filters;
	}

	public void setFilters(final Class<? extends ActionFilter>... filters) {
		this.filters = filters;
	}

	public String[] getActionMethodNames() {
		return actionMethodNames;
	}

	public void setActionMethodNames(final String... actionMethodNames) {
		this.actionMethodNames = actionMethodNames;
	}

	public Class<? extends ActionNamingStrategy> getNamingStrategy() {
		return namingStrategy;
	}

	public void setNamingStrategy(final Class<? extends ActionNamingStrategy> namingStrategy) {
		this.namingStrategy = namingStrategy;
	}
}
