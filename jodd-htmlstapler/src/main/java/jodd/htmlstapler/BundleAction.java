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

package jodd.htmlstapler;

import jodd.util.ArraysUtil;

import java.util.ArrayList;
import java.util.List;

import static jodd.htmlstapler.HtmlStaplerBundlesManager.Strategy.ACTION_MANAGED;

/**
 * Bundle action used during page parsing and resources collection.
 */
public class BundleAction {

	private static final String BUNDLE_ID_MARKER = "---jodd-bundle-id-marker---";
	private static final String UNSTAPLE_MARKER = "jodd-unstaple";

	protected final HtmlStaplerBundlesManager bundlesManager;
	protected final String bundleContentType;
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
	public BundleAction(final HtmlStaplerBundlesManager bundlesManager, final String servletPath, final String bundleContentType) {
		this.bundlesManager = bundlesManager;
		this.bundleContentType = bundleContentType;
		this.strategy = bundlesManager.getStrategy();

		String realActionPath = bundlesManager.resolveRealActionPath(servletPath);

		actionPath = realActionPath + '*' + bundleContentType;

		contextPath = bundlesManager.contextPath;

		if (strategy == ACTION_MANAGED) {
			bundleId = bundlesManager.lookupBundleId(actionPath);

			newAction = (bundleId == null);

			if (newAction) {
				sources = new ArrayList<>();
			}
		} else {
			bundleId = BUNDLE_ID_MARKER + bundleContentType;
			bundleIdMark = bundleId.toCharArray();
			newAction = true;
			sources = new ArrayList<>();
		}

		firstScriptTag = true;
	}

	/**
	 * Returns <code>true</code> if resource link  should be collected into the bundle. Returns
	 * <code>false</code> for resources that has to ignored or when no link existed (<code>null</code>).
	 * <p>
	 * By default, ignores resource links that contains "jodd.unstaple"
	 * (usually set as dummy parameter name).
	 */
	public boolean acceptLink(final String src) {
		if (src == null) {
			return false;
		}
		return !src.contains(UNSTAPLE_MARKER);
	}

	/**
	 * Process links. Returns bundle link if this is the first resource
	 * of the same type. Otherwise, returns <code>null</code> indicating
	 * that collection is going on and the original link should be removed.
	 */
	public String processLink(final String src) {
		if (newAction) {
			if (bundleId == null) {
				bundleId = bundlesManager.registerNewBundleId();
				bundleId += '.' + bundleContentType;
			}
			sources.add(src);
		}

		if (firstScriptTag) {
			// this is the first tag, change the url to point to the bundle
			firstScriptTag = false;

			return buildStaplerUrl();
		} else {
			// ignore all other script tags
			return null;
		}
	}

	/**
	 * Builds stapler URL based on bundle action data.
	 */
	protected String buildStaplerUrl() {
		return contextPath + '/' + bundlesManager.getStaplerPath() + '/' + bundleId;
	}

	/**
	 * Called on end of parsing.
	 */
	public void end() {
		if (newAction) {
			bundleId = bundlesManager.registerBundle(contextPath, actionPath, bundleId, bundleContentType, sources);
		}
	}

	/**
	 * Replaces bundle marker with calculated bundle id.
	 * Used for <code>RESOURCE_ONLY</code> strategy.
	 */
	public char[] replaceBundleId(final char[] content) {
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
