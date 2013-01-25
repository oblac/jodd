// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.introspector.ClassIntrospector;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionConfigSet;
import jodd.madvoc.MadvocException;
import jodd.madvoc.MadvocUtil;
import jodd.petite.meta.PetiteInject;
import jodd.util.ClassLoaderUtil;
import jodd.util.collection.SortedArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Manages all Madvoc action registrations.
 */
public class ActionsManager {

	private static final Logger log = LoggerFactory.getLogger(ActionsManager.class);

	@PetiteInject
	protected ActionMethodParser actionMethodParser;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	protected int actionsCount;
	protected final HashMap<String, ActionConfigSet> map;		// map of all action paths w/o macros
	protected final SortedArrayList<ActionConfigSet> list;		// list of all action paths with macros

	public ActionsManager() {
		this.map = new HashMap<String, ActionConfigSet>();
		this.list = new SortedArrayList<ActionConfigSet>(new ActionConfigSetComparator());
	}

	/**
	 * Comparator that considers first chunks number then action path.
	 */
	public static class ActionConfigSetComparator implements Comparator<ActionConfigSet> {

		public int compare(ActionConfigSet set1, ActionConfigSet set2) {
			int deep1 = set1.actionPathChunks.length;
			int deep2 = set2.actionPathChunks.length;

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
		Method method = ClassIntrospector.lookup(actionClass).getMethod(actionMethod);
		if (method == null) {
			throw new MadvocException("Provided action class '" + actionClass.getSimpleName() + "' doesn't contain public method '" + actionMethod + "'.");
		}
		return registerAction(actionClass, method, actionPath);
	}

	public ActionConfig register(Class actionClass, Method actionMethod, String actionPath) {
		return registerAction(actionClass, actionMethod, actionPath);
	}

	public ActionConfig register(Class actionClass, Method actionMethod) {
		return registerAction(actionClass, actionMethod, null);
	}

	// ---------------------------------------------------------------- registration

	/**
	 * Registration single point. Optionally, if action path with the same name already exist,
	 * exception will be thrown. Returns created {@link ActionConfig}.
	 */
	protected ActionConfig registerAction(Class actionClass, Method actionMethod, String actionPath) {
		ActionConfig cfg = actionMethodParser.parse(actionClass, actionMethod, actionPath);
		if (cfg == null) {
			return null;
		}

		if (log.isDebugEnabled()) {
			log.debug("Registering Madvoc action: " + cfg.actionPath + " to: " +
					cfg.actionClass.getName() + '#' + cfg.actionClassMethod.getName());
		}

		ActionConfigSet set = new ActionConfigSet(cfg.actionPath);

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
			if (map.containsKey(cfg.actionPath) == false) {
				map.put(cfg.actionPath, set);
			} else {
				set = map.get(cfg.actionPath);
			}

		}
		boolean isDuplicate = set.add(cfg);

		if (madvocConfig.isDetectDuplicatePathsEnabled()) {
			if (isDuplicate) {
				throw new MadvocException("Duplicate action path for " + cfg);
			}
		}

		if (isDuplicate == false) {
			actionsCount++;
		}
		return cfg;
	}

	// ---------------------------------------------------------------- look-up

	/**
	 * Returns action configurations for provided action path.
	 * First it lookups for exact <code>actionPath</code>.
	 * If action path is not registered, it is split into chunks
	 * and match against macros.
	 * Returns <code>null</code> if action path is not registered.
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

		String[] actionPathChunks = MadvocUtil.splitActionPath(actionPath);

		int len = list.size();

		int lastMatched = -1;
		int maxMatchedChars = -1;

	loop:
		for (int i = 0; i < len; i++) {
			actionConfigSet = list.get(i);
			int deep = actionConfigSet.actionPathChunks.length;
			if (deep < actionPathChunks.length) {
				continue;
			}
			if (deep > actionPathChunks.length) {
				break;
			}

			// equal number of chunks, match one by one

			int totalMatchedChars = 0;
			for (int j = 0; j < deep; j++) {
				String chunk = actionPathChunks[j];

				int matchedChars = matchChunk(
						actionConfigSet.actionPathChunks[j],
						actionConfigSet.actionPathMacros[j],
						chunk);

				if (matchedChars == -1) {
					continue loop;
				}

				totalMatchedChars += matchedChars;
			}

			if (totalMatchedChars > maxMatchedChars) {
				maxMatchedChars = totalMatchedChars;
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
	 * Match action path chunk with current chunk.
	 * Returns number of additional chars matched (0 or more);
	 * or -1 if chunks are not matched.
	 */
	protected int matchChunk(String actionPathChunk, ActionConfigSet.PathMacro macro, String chunk) {

		if (macro == null) {
			// there is no macro at this level, just check strings
			if (actionPathChunk.equals(chunk)) {
				return chunk.length();
			}
			return -1;
		}

		// detect macro

		int matchedChars = 0;

		if (chunk.startsWith(macro.left) == false) {
			return -1;
		}
		matchedChars += macro.left.length();

		if (chunk.endsWith(macro.right) == false) {
			return -1;
		}
		matchedChars += macro.right.length();

		// match value
		if (macro.pattern != null) {
			String value = chunk.substring(macro.left.length(), chunk.length() - macro.right.length());

			if (macro.pattern.matcher(value).matches() == false) {
				return -1;
			}
		}

		// macro found
		return matchedChars;
	}

}
