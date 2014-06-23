// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.config;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionDef;
import jodd.madvoc.component.ActionMethodParser;
import jodd.madvoc.component.ActionsManager;
import jodd.madvoc.component.MadvocConfig;
import jodd.madvoc.component.ResultsManager;
import jodd.madvoc.filter.ActionFilter;
import jodd.madvoc.interceptor.ActionInterceptor;
import jodd.madvoc.meta.Action;
import jodd.madvoc.result.ActionResult;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Method;

/**
 * Madvoc configurator for manual configuration.
 */
public abstract class ManualMadvocConfigurator implements MadvocConfigurator {

	public static final String NONE = Action.NONE;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	@PetiteInject
	protected ActionsManager actionsManager;

	@PetiteInject
	protected ActionMethodParser actionMethodParser;

	@PetiteInject
	protected ResultsManager resultsManager;

	// ---------------------------------------------------------------- results

	/**
	 * Registers result class.
	 */
	public void result(Class<? extends ActionResult> resultClass) {
		resultsManager.register(resultClass);
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
		Method actionClassMethod;
		String actionMethodString;
		String alias;
		ActionFilter[] actionFilters;
		ActionInterceptor[] actionInterceptors;
		boolean async;

		/**
		 * Defines action path.
		 */
		public ActionBuilder path(String path) {
			this.actionPath = path;
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
		 * Defines set of interceptors.
		 */
		public ActionBuilder interceptedBy(ActionInterceptor... interceptors) {
			this.actionInterceptors = interceptors;
			return this;
		}

		/**
		 * Defines set of filters.
		 */
		public ActionBuilder filtereBy(ActionFilter... filters) {
			this.actionFilters = filters;
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
		 * Defines async execution flag.
		 */
		public ActionBuilder async(boolean async) {
			this.async = async;
			return this;
		}

		/**
		 * Binds and finalize action configuration.
		 */
		public void bind() {
			if (actionMethodString != null) {
				actionClassMethod = actionsManager.resolveActionMethod(actionClass, actionMethodString);
			}

			ActionConfig actionConfig =
					actionMethodParser.createActionConfig(
							actionClass, actionClassMethod,
							actionFilters, actionInterceptors,
							new ActionDef(actionPath, method), async);

			actionsManager.registerAction(actionConfig);

			if (alias != null) {
				actionsManager.registerPathAlias(actionPath, alias);
			}
		}

	}

}