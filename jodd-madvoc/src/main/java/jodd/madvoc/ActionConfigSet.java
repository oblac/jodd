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
		List<ActionConfig> list = new ArrayList<>(configs.length);
		Collections.addAll(list, configs);
		return list;
	}

	/**
	 * Adds action configuration. Returns <code>true</code> if
	 * new configuration replaces existing one.
	 */
	public boolean add(ActionConfig cfg) {
		if (!cfg.actionPath.equals(this.actionPath)) {
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