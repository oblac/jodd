// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.component;

import jodd.introspector.ClassIntrospector;
import jodd.log.Log;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.ActionConfigSet;
import jodd.madvoc.MadvocException;
import jodd.petite.meta.PetiteInject;
import jodd.util.BinarySearch;
import jodd.util.ClassLoaderUtil;
import jodd.util.collection.SortedArrayList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manages all Madvoc action registrations.
 */
public class ActionsManager {

	private static final Log log = Log.getLogger(ActionsManager.class);

	@PetiteInject
	protected ActionMethodParser actionMethodParser;

	@PetiteInject
	protected MadvocConfig madvocConfig;

	protected int actionsCount;
	protected final HashMap<String, ActionConfigSet> map;
	protected final SortedArrayList<ActionConfigSet> list;
	protected final ActionPathChunksBinarySearch listMatch;
	protected final BinarySearch<ActionConfigSet> listBS;

	public ActionsManager() {
		this.map = new HashMap<String, ActionConfigSet>();
		this.list = new SortedArrayList<ActionConfigSet>();
		listMatch = new ActionPathChunksBinarySearch();
		listBS = BinarySearch.forList(list);
	}

	/**
	 * Returns all registered action configurations.
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
			throw new MadvocException("Madvoc action signature syntax error: '" + actionSignature + "'.");
		}
		String actionClassName = actionSignature.substring(0, ndx);
		String actionMethodName = actionSignature.substring(ndx + 1);
		Class actionClass;
		try {
			actionClass = ClassLoaderUtil.loadClass(actionClassName, this.getClass());
		} catch (ClassNotFoundException cnfex) {
			throw new MadvocException("Madvoc action class not found: '" + actionClassName + "'.", cnfex);
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
			int ndx = listBS.find(set);
			if (ndx < 0) {
				list.add(set);
			} else {
				set = list.get(ndx);
			}
		} else {
			if (map.containsKey(cfg.actionPath) == false) {
				map.put(cfg.actionPath, set);
			} else {
				set = map.get(cfg.actionPath);
			}

		}
		boolean isDuplicate = set.add(cfg);
		if (isDuplicate == false) {
			actionsCount++;
		}

		if (madvocConfig.isDetectDuplicatePathsEnabled()) {
			if (isDuplicate) {
				throw new MadvocException("Duplicate action path for '" + cfg + "'.");
			}
		}
		return cfg;
	}

	// ---------------------------------------------------------------- look-up

	/**
	 * Returns action configurations for provided action path.
	 * Returns <code>null</code> if action path is not registered.
	 */
	public ActionConfig lookup(String actionPath, String[] actionChunks, String method) {
		// 1st try: the map
		ActionConfigSet acset = map.get(actionPath);
		if (acset != null) {
			ActionConfig actionConfig = acset.lookup(method);
			if (actionConfig != null) {
				return actionConfig;
			}
		}

		// 2nd try: the list
		int low = 0;
		int high = list.size() - 1;
		int macroNdx = 0;
		for (int deep = 0; deep < actionChunks.length; deep++) {
			String chunk = actionChunks[deep];
			listMatch.deep = deep;

			int nextLow = listMatch.findFirst(chunk, low, high);
			if (nextLow < 0) {
				// there is no exact match, check if there is a macro on low index
				int matched = matchChunk(chunk, deep, macroNdx, low, high);
				if (matched == -1) {
					low = nextLow;
					break;
				} else {
					// macro found, continue
					macroNdx++;
					low = matched;
					high = matched;
				}
			} else {
				// exact match found, proceed
				low = nextLow;
				if (high > low) {
					high = listMatch.findLast(chunk, low, high);
				}
			}
		}
		if (low < 0) {
			return null;
		}

		ActionConfigSet set = list.get(low);
		ActionConfig cfg = set.lookup(method);

		if (cfg == null) {
			return null;
		}
		if (set.actionPathChunks.length != actionChunks.length) {
			return null;
		}
		return cfg;
	}


	protected int matchChunk(String chunk, int chunkNdx, int macroNdx, int low, int high) {

		for (int i = low; i <= high; i++) {
			ActionConfigSet set = list.get(i);

			// check if there is a macro on this chunk position
			if (macroNdx >= set.actionPathMacros.length) {
				continue;
			}
			ActionConfigSet.PathMacro macro = set.actionPathMacros[macroNdx];
			if (macro.ndx != chunkNdx) {
				continue;
			}

			// match macro
			if (chunk.startsWith(macro.left) == false) {
				continue;
			}
			if (chunk.endsWith(macro.right) == false) {
				continue;
			}

			// match value
			if (macro.pattern != null) {
				String value = chunk.substring(macro.left.length(), chunk.length() - macro.right.length());
				if (macro.pattern.matcher(value).matches() == false) {
					continue;
				}
			}

			// macro found
			return i;
		}
		return -1;
	}

	/**
	 * Binary search for action paths chunks.
	 */
	protected class ActionPathChunksBinarySearch extends BinarySearch<String> {

		protected int deep;

		/**
		 * Returns chunk <code>deep</code> of a path at <code>index</code>.
		 */
		protected String get(int index, int deep) {
			return list.get(index).actionPathChunks[deep];
		}

		@Override
		protected int compare(int index, String element) {
			return get(index, deep).compareTo(element);
		}

		@Override
		protected int getLastIndex() {
			return list.size() - 1;
		}
	}

}
