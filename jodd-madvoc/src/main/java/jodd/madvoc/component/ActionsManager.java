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
import jodd.madvoc.config.ActionRuntimeSet;
import jodd.madvoc.config.ActionRuntimeSetComparator;
import jodd.madvoc.macro.PathMacros;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassLoaderUtil;
import jodd.util.StringUtil;
import jodd.util.collection.SortedArrayList;

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
	protected ActionPathMacroManager actionPathMacroManager;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	protected int actionsCount;
	protected boolean asyncMode;
	protected final HashMap<String, ActionRuntimeSet> map;		// map of all action paths w/o macros
	protected final SortedArrayList<ActionRuntimeSet> list;		// list of all action paths with macros
	protected final HashMap<String, ActionRuntime> runtimes;	// another map of all action runtimes
	protected Map<String, String> pathAliases;					// path aliases

	public ActionsManager() {
		this.map = new HashMap<>();
		this.list = new SortedArrayList<>(new ActionRuntimeSetComparator());
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

		for (ActionRuntimeSet set : map.values()) {
			all.addAll(set.getActionRuntimes());
		}
		for (ActionRuntimeSet set : list) {
			all.addAll(set.getActionRuntimes());
		}
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

	// ---------------------------------------------------------------- register variations

	/**
	 * Resolves action method for given action class ane method name.
	 */
	public Method resolveActionMethod(Class<?> actionClass, String methodName) {
		MethodDescriptor methodDescriptor = ClassIntrospector.get().lookup(actionClass).getMethodDescriptor(methodName, false);
		if (methodDescriptor == null) {
			throw new MadvocException("Public method not found: " + actionClass.getSimpleName() + "#" + methodName);
		}
		return methodDescriptor.getMethod();
	}

	/**
	 * Registers action with provided action signature.
	 */
	public ActionRuntime register(String actionSignature) {
		return register(actionSignature, null);
	}

	/**
	 * Registers action with provided action signature.
	 */
	public ActionRuntime register(String actionSignature, ActionDefinition actionDefinition) {
		int ndx = actionSignature.indexOf('#');
		if (ndx == -1) {
			throw new MadvocException("Madvoc action signature syntax error: " + actionSignature);
		}
		String actionClassName = actionSignature.substring(0, ndx);
		String actionMethodName = actionSignature.substring(ndx + 1);
		Class actionClass;
		try {
			actionClass = ClassLoaderUtil.loadClass(actionClassName);
		} catch (ClassNotFoundException cnfex) {
			throw new MadvocException("Madvoc action class not found: " + actionClassName, cnfex);
		}
		return register(actionClass, actionMethodName, actionDefinition);
	}

	public ActionRuntime register(Class actionClass, Method actionMethod) {
		return registerAction(actionClass, actionMethod, null);
	}

	public ActionRuntime register(Class actionClass, Method actionMethod, ActionDefinition actionDefinition) {
		return registerAction(actionClass, actionMethod, actionDefinition);
	}

	/**
	 * Registers action with provided action class and method name.
	 */
	public ActionRuntime register(Class actionClass, String actionMethodName) {
		Method actionMethod = resolveActionMethod(actionClass, actionMethodName);
		return registerAction(actionClass, actionMethod, null);
	}

	public ActionRuntime register(Class actionClass, String actionMethodName, ActionDefinition actionDefinition) {
		Method actionMethod = resolveActionMethod(actionClass, actionMethodName);
		return registerAction(actionClass, actionMethod, actionDefinition);
	}

	// ---------------------------------------------------------------- registration

	/**
	 * Registration main point. Does two things:
	 * <ul>
	 *     <li>{@link jodd.madvoc.component.ActionMethodParser#parse(Class, java.lang.reflect.Method, ActionDefinition) parse action}
	 *     and creates {@link ActionRuntime}</li>
	 *     <li>{@link #registerAction(ActionRuntime) registers} created {@link ActionRuntime}</li>
	 * </ul>
	 * Returns created {@link ActionRuntime}.
	 * @see #registerAction(ActionRuntime)
	 */
	protected ActionRuntime registerAction(Class actionClass, Method actionMethod, ActionDefinition actionDefinition) {
		ActionRuntime actionRuntime = actionMethodParser.parse(actionClass, actionMethod, actionDefinition);
		if (actionRuntime == null) {
			return null;
		}
		return registerAction(actionRuntime);
	}

	/**
	 * Registers manually created {@link ActionRuntime action runtime configurations}.
	 * Optionally, if action path with the same name already exist,
	 * exception will be thrown.
	 */
	public ActionRuntime registerAction(ActionRuntime actionRuntime) {
		final String actionPath = actionRuntime.actionPath();
		final String method = actionRuntime.actionMethod();

		log.debug(() -> "Madvoc action: " + ifNotNull(method, m -> m + " ") + actionRuntime.actionPath() + " => " + actionRuntime.actionString());

		ActionRuntimeSet set = createActionRuntimeSet(actionRuntime.actionPath());

		if (set.actionPathMacros() != null) {
			// new action patch contain macros
			int ndx = -1;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).actionPath().equals(actionPath)) {
					ndx = i;
					break;
				}
			}
			if (ndx < 0) {
				list.add(set);
			} else {
				set = list.get(ndx);
			}
		} else {
			// action path is without macros
			if (!map.containsKey(actionRuntime.actionPath())) {
				map.put(actionRuntime.actionPath(), set);
			} else {
				set = map.get(actionRuntime.actionPath());
			}

		}
		boolean isDuplicate = set.add(actionRuntime);

		if (madvocConfig.isDetectDuplicatePathsEnabled()) {
			if (isDuplicate) {
				throw new MadvocException("Duplicate action path for " + actionRuntime);
			}
		}

		// finally

		runtimes.put(actionRuntime.actionString(), actionRuntime);

		if (!isDuplicate) {
			actionsCount++;
		}

		// async check
		if (actionRuntime.async()) {
			asyncMode = true;
		}

		return actionRuntime;
	}

	/**
	 * Creates new action runtime set from the action path.
	 */
	protected ActionRuntimeSet createActionRuntimeSet(String actionPath) {
		PathMacros pathMacros = actionPathMacroManager.buildActionPathMacros(actionPath);

		return new ActionRuntimeSet(actionPath, pathMacros);
	}

	// ---------------------------------------------------------------- look-up

	/**
	 * Returns action runtime configurations for provided action path.
	 * First it lookups for exact <code>actionPath</code>.
	 * If action path is not registered, it is split into chunks
	 * and match against macros.
	 * Returns <code>null</code> if action path is not registered.
	 * <code>method</code> must be in uppercase.
	 */
	public ActionRuntime lookup(String actionPath, String method) {

		// 1st try: the map

		ActionRuntimeSet actionRuntimeSet = map.get(actionPath);
		if (actionRuntimeSet != null) {
			ActionRuntime actionRuntime = actionRuntimeSet.lookup(method);
			if (actionRuntime != null) {
				return actionRuntime;
			}
		}

		// 2nd try: the list

		int actionPathDeep = StringUtil.count(actionPath, '/');

		int len = list.size();

		int lastMatched = -1;
		int maxMatchedChars = -1;

		for (int i = 0; i < len; i++) {
			actionRuntimeSet = list.get(i);

			int deep = actionRuntimeSet.deep();
			if (deep < actionPathDeep) {
				continue;
			}
			if (deep > actionPathDeep) {
				break;
			}

			// same deep level, try the fully match

			int matchedChars = actionRuntimeSet.actionPathMacros().match(actionPath);

			if (matchedChars == -1) {
				continue;
			}

			if (matchedChars > maxMatchedChars) {
				maxMatchedChars = matchedChars;
				lastMatched = i;
			}
		}

		if (lastMatched < 0) {
			return null;
		}

		ActionRuntimeSet set = list.get(lastMatched);

		return set.lookup(method);
	}

	/**
	 * Lookups action runtime config for given action class and method string (aka 'action string').
	 * The action string has the following format: <code>className#methodName</code>.
	 * @see ActionRuntime#actionString()
	 */
	public ActionRuntime lookup(String actionString) {
		return runtimes.get(actionString);
	}

	// ---------------------------------------------------------------- aliases

	/**
	 * Registers new path alias.
	 */
	public void registerPathAlias(String alias, String path) {
		pathAliases.put(alias, path);
	}

	/**
	 * Returns path alias.
	 */
	public String lookupPathAlias(String alias) {
		return pathAliases.get(alias);
	}

}