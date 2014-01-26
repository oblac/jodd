// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.madvoc.macro.PathMacros;
import jodd.util.ArraysUtil;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set of {@link ActionConfig action configs} with the same action path
 * but with different http method.
 */
public class ActionConfigSet implements Comparable<ActionConfigSet> {

	protected ActionConfig[] configs = new ActionConfig[0];

	// action path
	public final String actionPath;
	// simple count of '/', for faster matching
	public final int deep;
	// macros
	public final PathMacros actionPathMacros;

	/**
	 * Creates new action config set. It is set of <code>ActionConfig</code>s, i.e. Madvoc
	 * actions, with the same action path and different http method.
	 *
	 * @param actionPath action path
	 * @param pathMacros action path macros if existing any or <code>null</code>
	 */
	public ActionConfigSet(String actionPath, PathMacros pathMacros) {
		this.actionPath = actionPath;
		this.deep = StringUtil.count(actionPath, '/');
		this.actionPathMacros = pathMacros;
	}

	/**
	 * Returns a new list of all action configs from this set.
	 */
	public List<ActionConfig> getActionConfigs() {
		List<ActionConfig> list = new ArrayList<ActionConfig>(configs.length);
		Collections.addAll(list, configs);
		return list;
	}

	/**
	 * Adds action configuration. Returns <code>true</code> if
	 * new configuration replaces existing one.
	 */
	public boolean add(ActionConfig cfg) {
		if (cfg.actionPath.equals(this.actionPath) == false) {
			throw new MadvocException("Invalid configuration");
		}

		cfg.actionConfigSet = this;

		int ndx = lookupIndex(cfg.actionMethod);
		if (ndx == -1) {
			if (cfg.actionMethod == null) {
				configs = ArraysUtil.append(configs, cfg);
			} else {
				configs = ArraysUtil.insert(configs, cfg, 0);
			}
			return false;
		} else {
			configs[ndx] = cfg;
			return true;
		}
	}

	/**
	 * Lookup for action config for given method.
	 */
	public ActionConfig lookup(String method) {
		int ndx = lookupIndex(method);
		if (ndx == -1) {
			return null;
		}
		return configs[ndx];
	}

	protected int lookupIndex(String method) {

		for (int i = 0; i < configs.length; i++) {
			ActionConfig config = configs[i];

			if (config.actionMethod == null) {
				return i;
			}
			if (config.actionMethod.equals(method)) {
				return i;
			}
		}
		return -1;
	}

	// ---------------------------------------------------------------- compare

	public int compareTo(ActionConfigSet set) {
		return this.actionPath.compareTo(set.actionPath);
	}

}