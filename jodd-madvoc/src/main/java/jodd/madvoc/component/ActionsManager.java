// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionConfigSet;
import jodd.madvoc.MadvocException;
import jodd.madvoc.macro.PathMacros;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassLoaderUtil;
import jodd.util.StringUtil;
import jodd.util.collection.SortedArrayList;
import jodd.log.Logger;
import jodd.log.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	protected final HashMap<String, ActionConfigSet> map;		// map of all action paths w/o macros
	protected final SortedArrayList<ActionConfigSet> list;		// list of all action paths with macros
	protected final HashMap<String, ActionConfig> configs;		// another map of all action configs
	protected Map<String, String> pathAliases;					// path aliases

	public ActionsManager() {
		this.map = new HashMap<String, ActionConfigSet>();
		this.list = new SortedArrayList<ActionConfigSet>(new ActionConfigSetComparator());
		this.pathAliases = new HashMap<String, String>();
		this.configs = new HashMap<String, ActionConfig>();
		this.asyncMode = false;
	}

	/**
	 * Comparator that considers first chunks number then action path.
	 */
	public static class ActionConfigSetComparator implements Comparator<ActionConfigSet> {

		public int compare(ActionConfigSet set1, ActionConfigSet set2) {
			int deep1 = set1.deep;
			int deep2 = set2.deep;

			if (deep1 == deep2) {
				return set1.actionPath.compareTo(set2.actionPath);
			}
			return deep1 - deep2;
		}
	}

	/**
	 * Returns all registered action configurations.
	 * Returned list is a join of action paths
	 * with and without the macro.
	 */
	public List<ActionConfig> getAllActionConfigurations() {
		List<ActionConfig> all = new ArrayList<ActionConfig>(actionsCount);

		for (ActionConfigSet set : map.values()) {
			all.addAll(set.getActionConfigs());
		}
		for (ActionConfigSet set : list) {
			all.addAll(set.getActionConfigs());
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
	 * Registers action with provided action signature.
	 */
	public ActionConfig register(String actionSignature) {
		return register(actionSignature, null);
	}

	/**
	 * Registers action with provided action signature.
	 */
	public ActionConfig register(String actionSignature, String actionPath) {
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
		return register(actionClass, actionMethodName, actionPath);
	}

	/**
	 * Registers action with provided action class and method name.
	 */
	public ActionConfig register(Class actionClass, String actionMethod) {
		return register(actionClass, actionMethod, null);
	}

	/**
	 * Registers action with provided action path, class and method name.
	 */
	public ActionConfig register(Class actionClass, String actionMethod, String actionPath) {
		return registerAction(actionClass, actionMethod, actionPath);
	}

	public ActionConfig register(Class actionClass, Method actionMethod, String actionPath) {
		return registerAction(actionClass, actionMethod, actionPath);
	}

	public ActionConfig register(Class actionClass, Method actionMethod) {
		return registerAction(actionClass, actionMethod, null);
	}

	// ---------------------------------------------------------------- registration

	/**
	 * Registration main point. Optionally, if action path with the same name already exist,
	 * exception will be thrown. Returns created {@link ActionConfig}.
	 */
	protected ActionConfig registerAction(Class actionClass, Method actionMethod, String actionPath) {
		ActionConfig actionConfig = actionMethodParser.parse(actionClass, actionMethod, actionPath);
		if (actionConfig == null) {
			return null;
		}
		return registerAction(actionConfig);
	}

	/**
	 * @see #registerAction(Class, java.lang.reflect.Method, String)
	 */
	protected ActionConfig registerAction(Class actionClass, String actionMethodName, String actionPath) {
		ActionConfig actionConfig = actionMethodParser.parse(actionClass, actionMethodName, actionPath);
		if (actionConfig == null) {
			return null;
		}
		return registerAction(actionConfig);
	}

	/**
	 * Registers manually created {@link ActionConfig action configurations}.
	 */
	public ActionConfig registerAction(ActionConfig actionConfig) {
		String actionPath = actionConfig.actionPath;

		if (log.isDebugEnabled()) {
			log.debug("Registering Madvoc action: " + actionConfig.actionPath + " to: " +
					actionConfig.getActionString());
		}

		ActionConfigSet set = createActionConfigSet(actionConfig.actionPath);

		if (set.actionPathMacros != null) {
			// new action patch contain macros
			int ndx = -1;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).actionPath.equals(actionPath)) {
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
			if (map.containsKey(actionConfig.actionPath) == false) {
				map.put(actionConfig.actionPath, set);
			} else {
				set = map.get(actionConfig.actionPath);
			}

		}
		boolean isDuplicate = set.add(actionConfig);

		if (madvocConfig.isDetectDuplicatePathsEnabled()) {
			if (isDuplicate) {
				throw new MadvocException("Duplicate action path for " + actionConfig);
			}
		}

		// finally

		configs.put(actionConfig.getActionString(), actionConfig);

		if (isDuplicate == false) {
			actionsCount++;
		}

		// async check
		if (actionConfig.isAsync()) {
			asyncMode = true;
		}

		return actionConfig;
	}

	/**
	 * Creates new action config set from the action path.
	 */
	protected ActionConfigSet createActionConfigSet(String actionPath) {
		PathMacros pathMacros = actionPathMacroManager.buildActionPathMacros(actionPath);

		return new ActionConfigSet(actionPath, pathMacros);
	}

	// ---------------------------------------------------------------- look-up

	/**
	 * Returns action configurations for provided action path.
	 * First it lookups for exact <code>actionPath</code>.
	 * If action path is not registered, it is split into chunks
	 * and match against macros.
	 * Returns <code>null</code> if action path is not registered.
	 * <code>method</code> must be in uppercase.
	 */
	public ActionConfig lookup(String actionPath, String method) {

		// 1st try: the map

		ActionConfigSet actionConfigSet = map.get(actionPath);
		if (actionConfigSet != null) {
			ActionConfig actionConfig = actionConfigSet.lookup(method);
			if (actionConfig != null) {
				return actionConfig;
			}
		}

		// 2nd try: the list

		int actionPathDeep = StringUtil.count(actionPath, '/');

		int len = list.size();

		int lastMatched = -1;
		int maxMatchedChars = -1;

		for (int i = 0; i < len; i++) {
			actionConfigSet = list.get(i);

			int deep = actionConfigSet.deep;
			if (deep < actionPathDeep) {
				continue;
			}
			if (deep > actionPathDeep) {
				break;
			}

			// same deep level, try the fully match

			int matchedChars = actionConfigSet.actionPathMacros.match(actionPath);

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

		ActionConfigSet set = list.get(lastMatched);

		return set.lookup(method);
	}

	/**
	 * Lookups action config for given action class and method string (aka 'action string').
	 * The action string has the following format: <code>className#methodName</code>.
	 * @see jodd.madvoc.ActionConfig#getActionString()
	 */
	public ActionConfig lookup(String actionString) {
		return configs.get(actionString);
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