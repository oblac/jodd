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

import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.FiltersManager;
import jodd.madvoc.component.InterceptorsManager;
import jodd.madvoc.component.MadvocComponentLifecycle;
import jodd.madvoc.config.ActionDefinition;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;
import jodd.util.ArraysUtil;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Madvoc configurator for manual configuration.
 */
public abstract class MadvocRouter implements MadvocComponentLifecycle.Start {

	/**
	 * Creates new instance of {@link MadvocRouter}.
	 * Created instance is NOT wired with dependencies!
	 */
	public static MadvocRouter create() {
		return new MadvocRouter() {
			@Override
			public void start() {
			}
		};
	}

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected ActionMethodParser actionMethodParser;

	@PetiteInject
	protected FiltersManager filtersManager;

	@PetiteInject
	protected InterceptorsManager interceptorsManager;


	// ---------------------------------------------------------------- wrappers

	/**
	 * Configures an interceptor.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ActionInterceptor> MadvocRouter interceptor(Class<T> actionInterceptorClass) {
		interceptorsManager.resolve(actionInterceptorClass);
		return this;
	}

	/**
	 * Configures an interceptor.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ActionInterceptor> MadvocRouter interceptor(Class<T> actionInterceptorClass, Consumer<T> interceptorConsumer) {
		T interceptor = (T) interceptorsManager.resolve(actionInterceptorClass);
		interceptorConsumer.accept(interceptor);
		return this;
	}

	/**
	 * Returns action filter instance for further configuration.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ActionFilter> MadvocRouter filter(Class<T> actionFilterClass) {
		filtersManager.resolve(actionFilterClass);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ActionFilter> MadvocRouter filter(Class<T> actionFilterClass, Consumer<T> filterConsumer) {
		T filter = (T) filtersManager.resolve(actionFilterClass);
		filterConsumer.accept(filter);
		return this;
	}

	// ---------------------------------------------------------------- actions

	/**
	 * Starts with action registration i.e. route binding.
	 */
	public ActionBuilder route() {
		return new ActionBuilder();
	}

	/**
	 * Maps a GET path.
	 */
	public ActionBuilder get(String path) {
		return new ActionBuilder().path(path).httpMethod("GET");
	}
	/**
	 * Maps a POST path.
	 */
	public ActionBuilder post(String path) {
		return new ActionBuilder().path(path).httpMethod("POST");
	}
	/**
	 * Maps a PUT path.
	 */
	public ActionBuilder put(String path) {
		return new ActionBuilder().path(path).httpMethod("PUT");
	}
	/**
	 * Maps a DELETE path.
	 */
	public ActionBuilder delete(String path) {
		return new ActionBuilder().path(path).httpMethod("DELETE");
	}
	/**
	 * Maps an OPTION path.
	 */
	public ActionBuilder options(String path) {
		return new ActionBuilder().path(path).httpMethod("OPTIONS");
	}

	public class ActionBuilder {
		ActionHandler actionHandler;
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

		public ActionBuilder mapTo(ActionHandler actionHandler) {
			this.actionHandler = actionHandler;
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
		 * Binds and finalize action runtime configuration.
		 */
		public MadvocRouter bind() {
			final ActionConfig actionConfig = madvocConfig.getActionConfig();

			if (actionMethodString != null) {
				actionClassMethod = actionsManager.resolveActionMethod(actionClass, actionMethodString);
			}

			ActionFilter[] actionFilterInstances = filtersManager.resolveAll(actionConfig, actionFilters);

			ActionInterceptor[] actionInterceptorInstances = interceptorsManager.resolveAll(actionConfig, actionInterceptors);

			ActionDefinition actionDefinition;
			if (resultBasePath != null) {
				actionDefinition = new ActionDefinition(actionPath, method, resultBasePath);
			}
			else {
				actionDefinition = new ActionDefinition(actionPath, method);
			}

			ActionRuntime actionRuntime =
					actionMethodParser.createActionRuntime(
							actionHandler,
							actionClass, actionClassMethod,
							actionResult,
							actionFilterInstances, actionInterceptorInstances,
							actionDefinition, async,
							actionConfig
							);

			actionsManager.registerAction(actionRuntime);

			if (alias != null) {
				actionsManager.registerPathAlias(alias, actionPath);
			}

			return MadvocRouter.this;
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