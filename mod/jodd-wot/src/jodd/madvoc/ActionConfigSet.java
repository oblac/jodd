// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.util.ArraysUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Set of {@link ActionConfig action configs} with the same action path
 * but with different http method.
 */
public class ActionConfigSet implements Comparable<ActionConfigSet> {

	protected ActionConfig[] configs = new ActionConfig[0];
	public final String actionPath;
	public final String[] actionPathChunks;
	public final PathMacro[] actionPathMacros;

	public ActionConfigSet(String actionPath) {
		this.actionPath = actionPath;
		this.actionPathChunks = MadvocUtil.splitActionPath(actionPath);
		this.actionPathMacros = resolveMacros(actionPathChunks);
	}

	/**
	 * Returns a new list of all action configs from this set.
	 */
	public List<ActionConfig> getActionConfigs() {
		List<ActionConfig> list = new ArrayList<ActionConfig>(configs.length);
		for (ActionConfig config : configs) {
			list.add(config);
		}
		return list;
	}

	/**
	 * Adds action configuration. Returns <code>true</code> if
	 * new configuration replaces existing one.
	 */
	public boolean add(ActionConfig cfg) {
		if (cfg.actionPath.equals(this.actionPath) == false) {
			throw new MadvocException("Invalid configuration.");
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

	// ---------------------------------------------------------------- macros

	/**
	 * Finds the best matching macro.
	 */
	protected PathMacro[] resolveMacros(String[] chunks) {
		List<PathMacro> list = new ArrayList<PathMacro>(chunks.length);

		for (int i = 0; i < chunks.length; i++) {
			String chunk = chunks[i];

			int[] ndx = StringUtil.indexOfRegion(chunk, StringPool.DOLLAR_LEFT_BRACE, StringPool.RIGHT_BRACE);
			if (ndx != null) {
				PathMacro macro = new PathMacro();

				String name = chunk.substring(ndx[1], ndx[2]);

				int colonNdx = name.indexOf(':');
				if (colonNdx != -1) {
					String pattern = name.substring(colonNdx + 1);
					macro.pattern = Pattern.compile(pattern);
					name = name.substring(0, colonNdx);
				}
				macro.name = name;
				macro.ndx = i;
				macro.left = (ndx[0] == 0 ? StringPool.EMPTY : chunk.substring(0, ndx[0]));
				macro.right = (ndx[3] == chunk.length() ? StringPool.EMPTY : chunk.substring(ndx[3]));

				list.add(macro);
			}
		}
		if (list.isEmpty()) {
			return null;
		}
		return list.toArray(new PathMacro[list.size()]);
	}

	public static class PathMacro {

		public int ndx;

		public String name;

		// left prefix to the macro
		public String left;

		// right suffix to the end or #method
		public String right;

		// regex pattern
		public Pattern pattern;
	}

}
