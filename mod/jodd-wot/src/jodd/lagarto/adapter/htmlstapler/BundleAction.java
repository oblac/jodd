// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.htmlstapler;

import jodd.servlet.DispatcherUtil;
import jodd.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Bundle action.
 */
public class BundleAction {

	protected final BundlesManager bundlesManager;
	protected final String bundleName;
	protected final boolean newAction;
	protected final String actionPath;
	protected final String contextPath;
	protected String bundleId;

	protected List<String> sources;
	protected boolean firstScriptTag;

	/**
	 * Creates new bundle action.
	 */
	public BundleAction(BundlesManager bundlesManager, HttpServletRequest request, String bundleName) {
		this.bundlesManager = bundlesManager;
		this.bundleName = bundleName;

		actionPath = DispatcherUtil.getServletPath(request) + '.' + bundleName;

		contextPath = ServletUtil.getContextPath(request);

		bundleId = bundlesManager.lookupBundleId(actionPath);

		newAction = (bundleId == null);

		if (newAction) {
			sources = new ArrayList<String>();
		}

		firstScriptTag = true;
	}

	/**
	 * Process link.
	 */
	public String processLink(String src) {
		if (newAction) {
			if (bundleId == null) {
				bundleId = bundlesManager.registerNewBundleId();
			}
			sources.add(src);
		}

		if (firstScriptTag == true) {
			// this is the first tag, change the url to point to the bundle
			firstScriptTag = false;
			return contextPath + bundlesManager.getStaplerServletPath() + "?id=" + bundleId;
		} else {
			// ignore all other script tags
			return null;
		}
	}

	/**
	 * Called on end of parsing.
	 */
	public void end() {
		if (newAction) {
			bundlesManager.registerBundle(actionPath, bundleId, sources);
		}
	}
}
