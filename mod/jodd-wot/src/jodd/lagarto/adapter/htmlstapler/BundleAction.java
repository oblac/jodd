// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.htmlstapler;

import jodd.servlet.DispatcherUtil;
import jodd.servlet.ServletUtil;
import jodd.util.ArraysUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

import static jodd.lagarto.adapter.htmlstapler.HtmlStaplerBundlesManager.Strategy.ACTION_MANAGED;

/**
 * Bundle action used during page parsing and resources collection.
 */
public class BundleAction {

	protected final HtmlStaplerBundlesManager bundlesManager;
	protected final String bundleName;
	protected final boolean newAction;
	protected final String actionPath;
	protected final String contextPath;
	protected final HtmlStaplerBundlesManager.Strategy strategy;
	protected String bundleId;
	protected char[] bundleIdMark;

	protected List<String> sources;
	protected boolean firstScriptTag;

	/**
	 * Creates new bundle action.
	 */
	public BundleAction(HtmlStaplerBundlesManager bundlesManager, HttpServletRequest request, String bundleName) {
		this.bundlesManager = bundlesManager;
		this.bundleName = bundleName;
		this.strategy = bundlesManager.getStrategy();

		String realActionPath = bundlesManager.resolveRealActionPath(DispatcherUtil.getServletPath(request));

		actionPath = realActionPath + '*' + bundleName;

		contextPath = ServletUtil.getContextPath(request);

		if (strategy == ACTION_MANAGED) {
			bundleId = bundlesManager.lookupBundleId(actionPath);

			newAction = (bundleId == null);

			if (newAction) {
				sources = new LinkedList<String>();
			}
		} else {
			bundleId = "jodd-bundle-action-" + bundleName;
			bundleIdMark = bundleId.toCharArray();
			newAction = true;
			sources = new LinkedList<String>();
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
			bundleId = bundlesManager.registerBundle(actionPath, bundleId, sources);
		}
	}

	/**
	 * Replaces bundle marker with calculated bundle id.
	 * Used for <code>RESOURCE_ONLY</code> strategy.
	 */
	public char[] replaceBundleId(char[] content) {
		if (strategy == ACTION_MANAGED || bundleId == null) {
			return content;
		}

		int index = ArraysUtil.indexOf(content, bundleIdMark);
		if (index == -1) {
			return content;
		}

		char[] bundleIdChars = bundleId.toCharArray();
		char[] result = new char[content.length - bundleIdMark.length + bundleIdChars.length];

		System.arraycopy(content, 0, result, 0, index);
		System.arraycopy(bundleIdChars, 0, result, index, bundleIdChars.length);
		System.arraycopy(content, index + bundleIdMark.length, result, index + bundleIdChars.length, content.length - bundleIdMark.length - index);

		return result;
	}

}
