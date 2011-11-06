// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.jsbundle;

import jodd.lagarto.Tag;
import jodd.lagarto.TagAdapter;
import jodd.lagarto.TagVisitor;
import jodd.servlet.DispatcherUtil;
import jodd.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * JS Bundle tag adapter parses HTML page and collects all information
 * about linking javascript files.
 */
public class JsBundleTagAdapter extends TagAdapter {

	protected final JsBundlesManager jsbManager;
	protected final boolean newAction;
	protected final String actionPath;
	protected final String contextPath;
	protected String bundleId;

	protected List<String> sources;
	protected boolean firstScriptTag;
	protected boolean insideConditionalComment;

	public JsBundleTagAdapter(JsBundlesManager jsBundlesManager, TagVisitor target, HttpServletRequest request) {
		super(target);

		jsbManager = jsBundlesManager;

		actionPath = DispatcherUtil.getServletPath(request);

		contextPath = ServletUtil.getContextPath(request);

		bundleId = jsbManager.lookupBundleId(actionPath);

		newAction = (bundleId == null);

		if (newAction) {
			sources = new ArrayList<String>();
		}
		firstScriptTag = true;
		insideConditionalComment = false;
	}

	@Override
	public void script(Tag tag, CharSequence body) {
		String src = tag.getAttributeValue("src", false);

		if ((src != null) && (insideConditionalComment == false)) {
			if (newAction) {
				if (bundleId == null) {
					bundleId = jsbManager.registerNewBundleId();
				}
				sources.add(src);
			}

			if (firstScriptTag == true) {
				// this is a first tag, change the url to point to the bundle
				firstScriptTag = false;
				tag.setAttributeValue("src", false, contextPath + jsbManager.getServletPath() + "?id=" + bundleId);
				super.script(tag, body);
				return;
			} else {
				// ignore all other script tags
				return;
			}
		}
		super.script(tag, body);
	}

	@Override
	public void condCommentStart(CharSequence conditionalComment, boolean isDownlevelHidden) {
		insideConditionalComment = true;
		super.condCommentStart(conditionalComment, isDownlevelHidden);
	}

	@Override
	public void condCommentEnd(CharSequence conditionalComment, boolean isDownlevelHidden) {
		insideConditionalComment = false;
		super.condCommentEnd(conditionalComment, isDownlevelHidden);
	}

	@Override
	public void end() {
		if (newAction) {
			jsbManager.registerBundle(actionPath, bundleId, sources);
		}
		super.end();
	}
}
