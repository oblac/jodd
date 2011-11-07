// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter.htmlstapler;

import jodd.lagarto.Tag;
import jodd.lagarto.TagAdapter;
import jodd.lagarto.TagVisitor;

import javax.servlet.http.HttpServletRequest;

/**
 * HTML Stapler tag adapter parses HTML page and collects all information
 * about linking javascript/css files.
 */
public class HtmlStaplerTagAdapter extends TagAdapter {

	protected final BundlesManager bundlesManager;
	protected final BundleAction jsBundleAction;
	protected final BundleAction cssBundleAction;

	protected boolean insideConditionalComment;

	public HtmlStaplerTagAdapter(TagVisitor target, HttpServletRequest request) {
		super(target);

		bundlesManager = BundlesManager.getBundlesManager(request);

		jsBundleAction = bundlesManager.start(request, "js");
		cssBundleAction = bundlesManager.start(request, "css");

		insideConditionalComment = false;
	}

	// ---------------------------------------------------------------- javascripts

	@Override
	public void script(Tag tag, CharSequence body) {
		if (insideConditionalComment == false) {
			String src = tag.getAttributeValue("src", false);

			if (src != null) {
				String link = jsBundleAction.processLink(src);
				if (link != null) {
					tag.setAttributeValue("src", false, link);
					super.script(tag, body);
				}
				return;
			}
		}
		super.script(tag, body);
	}

	// ---------------------------------------------------------------- css

	@Override
	public void tag(Tag tag) {
		if (insideConditionalComment == false) {
			if (tag.getName().equalsIgnoreCase("link")) {
				String type = tag.getAttributeValue("type", false);

				if (type.equalsIgnoreCase("text/css") == true) {
					String media = tag.getAttributeValue("media", false);

					if (media == null || media.contains("screen")) {
						String href = tag.getAttributeValue("href", false);

						String link = cssBundleAction.processLink(href);
						if (link != null) {
							tag.setAttribute("href", false, link);
							super.tag(tag);
						}
						return;
					}
				}
			}
		}
		super.tag(tag);
	}

	// ---------------------------------------------------------------- conditional comments

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

	// ---------------------------------------------------------------- end

	@Override
	public void end() {
		jsBundleAction.end();
		cssBundleAction.end();
		super.end();
	}
}
