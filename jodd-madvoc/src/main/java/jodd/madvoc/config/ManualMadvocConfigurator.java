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

package jodd.madvoc.config;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionDef;
import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.FiltersManager;
import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;
import jodd.util.ArraysUtil;

import java.lang.reflect.Method;

/**
 * Madvoc configurator for manual configuration.
 */
public abstract class ManualMadvocConfigurator implements MadvocConfigurator {

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected ActionMethodParser actionMethodParser;

	@PetiteInject
	protected ResultsManager resultsManager;

	@PetiteInject
	protected FiltersManager filtersManager;

	@PetiteInject
	protected InterceptorsManager interceptorsManager;

	// ---------------------------------------------------------------- results

	/**
	 * Registers result class.
	 */
	public void result(Class<? extends ActionResult> resultClass) {
		resultsManager.register(resultClass);
	}

	// ---------------------------------------------------------------- wrappers

	/**
	 * Returns interceptor instance for further configuration.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ActionInterceptor> T interceptor(Class<T> actionInterceptorClass) {
		return (T) interceptorsManager.resolve(actionInterceptorClass);
	}

	/**
	 * Returns action filter instance for further configuration.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ActionFilter> T filter(Class<T> actionFilterClass) {
		return (T) filtersManager.resolve(actionFilterClass);
	}

	// ---------------------------------------------------------------- actions

	/**
	 * Starts with action registration.
	 */
	public ActionBuilder action() {
		return new ActionBuilder();
	}

	public class ActionBuilder {
		String method;
		String actionPath;
		Class actionClass;
		Class<? extends ActionResult> actionResult;
		Method actionClassMethod;
		String actionMethodString;
		String alias;
		String resultBasePath;
		Class<? extends ActionFilter>[] actionFilters;
		Class<? extends ActionInterceptor>[] actionInterceptors;
		boolean async;

		/**
		 * Defines action path.
		 */
		public ActionBuilder path(String path) {
			this.actionPath = path;
			return this;
		}

		/**
		 * Defines HTTP method.
		 */
		public ActionBuilder httpMethod(String method) {
			this.method = method;
			return this;
		}

		/**
		 * Defines HTTP method name and action path.
		 * @see #path(String)
		 */
		public ActionBuilder path(String method, String path) {
			this.method = method;
			return path(path);
		}

		/**
		 * Defines action class and method to map.
		 */
		public ActionBuilder mapTo(Class actionClass, Method actionMethod) {
			this.actionClass = actionClass;
			this.actionClassMethod = actionMethod;
			this.actionMethodString = null;
			return this;
		}

		/**
		 * Defines action method. It must be declared in action class.
		 */
		public ActionBuilder mapTo(Method actionMethod) {
			this.actionClass = actionMethod.getDeclaringClass();
			this.actionClassMethod = actionMethod;
			this.actionMethodString = null;
			return this;
		}

		/**
		 * Defines action class and method to map.
		 */
		public ActionBuilder mapTo(Class actionClass, String actionMethodName) {
			this.actionClass = actionClass;
			this.actionClassMethod = null;
			this.actionMethodString = actionMethodName;
			return this;
		}

		/**
		 * Appends list of interceptors to the list of action interceptors.
		 */
		public ActionBuilder interceptBy(Class<? extends ActionInterceptor>... interceptors) {
			if (actionInterceptors == null) {
				actionInterceptors = interceptors;
			}
			else {
				actionInterceptors = ArraysUtil.join(actionInterceptors, interceptors);
			}
			return this;
		}
		/**
		 * Appends single interceptor to the list of action interceptors.
		 */
		public ActionBuilder interceptBy(Class<? extends ActionInterceptor> interceptor) {
			if (actionInterceptors == null) {
				actionInterceptors = new Class[]{interceptor};
			}
			else {
				actionInterceptors = ArraysUtil.append(actionInterceptors, interceptor);
			}
			return this;
		}

		/**
		 * Appends list of filter to the list of action filters.
		 */
		public ActionBuilder filterBy(Class<? extends ActionFilter>... filters) {
			if (actionFilters == null) {
				actionFilters = filters;
			}
			else {
				actionFilters = ArraysUtil.join(actionFilters, filters);
			}
			return this;
		}
		/**
		 * Appends single filter to the list of action filters.
		 */
		public ActionBuilder filterBy(Class<? extends ActionFilter> filter) {
			if (actionFilters == null) {
				actionFilters = new Class[]{filter};
			}
			else {
				actionFilters = ArraysUtil.append(actionFilters, filter);
			}
			return this;
		}

		/**
		 * Defines path alias.
		 */
		public ActionBuilder alias(String aliasPath) {
			this.alias = aliasPath;
			return this;
		}

		/**
		 * Defines action result for this action.
		 */
		public ActionBuilder renderWith(Class<? extends ActionResult> actionResult) {
			this.actionResult = actionResult;
			return this;
		}

		/**
		 * Defines async execution flag.
		 */
		public ActionBuilder async(boolean async) {
			this.async = async;
			return this;
		}

		/**
		 * Defines result base path.
		 */
		public ActionBuilder resultBase(String resultBasePath) {
			this.resultBasePath = resultBasePath;
			return this;
		}

		/**
		 * Binds and finalize action configuration.
		 */
		public void bind() {
			if (actionMethodString != null) {
				actionClassMethod = actionsManager.resolveActionMethod(actionClass, actionMethodString);
			}

			ActionFilter[] actionFilterInstances = filtersManager.resolveAll(actionFilters);

			ActionInterceptor[] actionInterceptorInstances = interceptorsManager.resolveAll(actionInterceptors);

			ActionDef actionDef;
			if (resultBasePath != null) {
				actionDef = new ActionDef(actionPath, method, resultBasePath);
			}
			else {
				actionDef = new ActionDef(actionPath, method);
			}

			ActionConfig actionConfig =
					actionMethodParser.createActionConfig(
							actionClass, actionClassMethod,
							actionResult,
							actionFilterInstances, actionInterceptorInstances,
							actionDef, async);

			actionsManager.registerAction(actionConfig);

			if (alias != null) {
				actionsManager.registerPathAlias(alias, actionPath);
			}
		}

		/**
		 * Returns <code>true</code> when minimum configuration is provided.
		 * If so, you can call {@link #bind()} to complete the binding.
		 * This indicates that action path etc is already set in the line.
		 */
		public boolean isSet() {
			return actionPath != null && actionMethodString != null;
		}
	}

}