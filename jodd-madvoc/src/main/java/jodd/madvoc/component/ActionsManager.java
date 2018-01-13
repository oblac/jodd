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

import jodd.introspector.ClassIntrospector;
import jodd.introspector.MethodDescriptor;
import jodd.log.Logger;
import jodd.log.LoggerFactory;
import jodd.madvoc.MadvocConfig;
import jodd.madvoc.MadvocException;
import jodd.madvoc.config.ActionDefinition;
import jodd.madvoc.config.ActionRuntime;
import jodd.madvoc.config.RouteChunk;
import jodd.madvoc.config.Routes;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jodd.util.StringUtil.ifNotNull;

/**
 * Manages all Madvoc action and aliases registrations.
 */
public class ActionsManager {

	private static final Logger log = LoggerFactory.getLogger(ActionsManager.class);

	@PetiteInject
	protected ActionMethodParser actionMethodParser;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	protected int actionsCount;
	protected boolean asyncMode;

	protected final Routes routes;
	protected final HashMap<String, ActionRuntime> runtimes;	// another map of all action runtimes
	protected Map<String, String> pathAliases;					// path aliases

	public ActionsManager() {
		this.routes = new Routes(() -> madvocConfig);

		this.pathAliases = new HashMap<>();
		this.runtimes = new HashMap<>();
		this.asyncMode = false;
	}

	/**
	 * Returns all registered action runtime configurations.
	 * Returned list is a join of action paths
	 * with and without the macro.
	 */
	public List<ActionRuntime> getAllActionRuntimes() {
		List<ActionRuntime> all = new ArrayList<>(actionsCount);

		all.addAll(runtimes.values());

		return all;
	}

	/**
	 * Returns total number of registered actions.
	 */
	public int getActionsCount() {
		return actionsCount;
	}

	/**
	 * Returns <code>true</code> if at least one action has
	 * async mode turned on.
	 */
	public boolean isAsyncModeOn() {
		return asyncMode;
	}

	// ---------------------------------------------------------------- registration

	/**
	 * Resolves action method for given action class ane method name.
	 */
	public Method resolveActionMethod(final Class<?> actionClass, final String methodName) {
		MethodDescriptor methodDescriptor = ClassIntrospector.get().lookup(actionClass).getMethodDescriptor(methodName, false);
		if (methodDescriptor == null) {
			throw new MadvocException("Public method not found: " + actionClass.getSimpleName() + "#" + methodName);
		}
		return methodDescriptor.getMethod();
	}

	/**
	 * Registers action with provided action class and method name.
	 * @see #registerAction(Class, Method, ActionDefinition)
	 */
	public ActionRuntime registerAction(final Class actionClass, final String actionMethodName, final ActionDefinition actionDefinition) {
		Method actionMethod = resolveActionMethod(actionClass, actionMethodName);
		return registerAction(actionClass, actionMethod, actionDefinition);
	}

	/**
	 * Registration main point. Does two things:
	 * <ul>
	 *     <li>{@link jodd.madvoc.component.ActionMethodParser#parse(Class, java.lang.reflect.Method, ActionDefinition) parse action}
	 *     and creates {@link ActionRuntime}</li>
	 *     <li>{@link #registerActionRuntime(ActionRuntime) registers} created {@link ActionRuntime}</li>
	 * </ul>
	 * Returns created {@link ActionRuntime}.
	 * @see #registerActionRuntime(ActionRuntime)
	 */
	public ActionRuntime registerAction(final Class actionClass, final Method actionMethod, final ActionDefinition actionDefinition) {
		ActionRuntime actionRuntime = actionMethodParser.parse(actionClass, actionMethod, actionDefinition);
		if (actionRuntime == null) {
			return null;
		}
		return registerActionRuntime(actionRuntime);
	}

	/**
	 * Registers manually created {@link ActionRuntime action runtime configurations}.
	 * Optionally, if action path with the same name already exist,
	 * exception will be thrown.
	 */
	public ActionRuntime registerActionRuntime(final ActionRuntime actionRuntime) {
		final String actionPath = actionRuntime.actionPath();
		final String method = actionRuntime.actionMethod();

		log.debug(() -> "Madvoc action: " + ifNotNull(method, m -> m + " ") + actionRuntime.actionPath() + " => " + actionRuntime.actionString());
		RouteChunk routeChunk = routes.registerPath(method, actionPath);

		if (routeChunk.value() != null) {
			// existing chunk
			if (madvocConfig.isDetectDuplicatePathsEnabled()) {
				throw new MadvocException("Duplicate action path for " + actionRuntime);
			}
		}
		else {
			actionsCount++;
		}

		routeChunk.bind(actionRuntime);

		// finally

		runtimes.put(actionRuntime.actionString(), actionRuntime);

		// async check
		if (actionRuntime.async()) {
			asyncMode = true;
		}

		return actionRuntime;
	}

	// ---------------------------------------------------------------- look-up

	public ActionRuntime lookup(final String method, final String[] actionPath) {
		return routes.lookup(method, actionPath);
	}

	/**
	 * Lookups action runtime config for given action class and method string (aka 'action string').
	 * The action string has the following format: <code>className#methodName</code>.
	 * @see ActionRuntime#actionString()
	 */
	public ActionRuntime lookup(final String actionString) {
		return runtimes.get(actionString);
	}

	// ---------------------------------------------------------------- aliases

	/**
	 * Registers new path alias.
	 */
	public void registerPathAlias(final String alias, final String path) {
		pathAliases.put(alias, path);
	}

	/**
	 * Returns path alias.
	 */
	public String lookupPathAlias(final String alias) {
		return pathAliases.get(alias);
	}

}