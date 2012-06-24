// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.htmlstapler;

import jodd.lagarto.Tag;
import jodd.lagarto.TagAdapter;
import jodd.lagarto.TagVisitor;

/**
 * HTML Stapler tag adapter parses HTML page and collects all information
 * about linking resource files.
 */
public class HtmlStaplerTagAdapter extends TagAdapter {

	protected final HtmlStaplerBundlesManager bundlesManager;
	protected final BundleAction jsBundleAction;
	protected final BundleAction cssBundleAction;

	protected boolean insideConditionalComment;

	public HtmlStaplerTagAdapter(HtmlStaplerBundlesManager bundlesManager, String servletPath, TagVisitor target) {
		super(target);

		this.bundlesManager = bundlesManager;

		jsBundleAction = bundlesManager.start(servletPath, "js");
		cssBundleAction = bundlesManager.start(servletPath, "css");

		insideConditionalComment = false;
	}

	// ---------------------------------------------------------------- javascripts

	@Override
	public void script(Tag tag, CharSequence body) {
		if (insideConditionalComment == false) {
			String src = tag.getAttributeValue("src", false);

			if (jsBundleAction.acceptLink(src)) {
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

				if (type != null && type.equalsIgnoreCase("text/css") == true) {
					String media = tag.getAttributeValue("media", false);

					if (media == null || media.contains("screen")) {
						String href = tag.getAttributeValue("href", false);

						if (cssBundleAction.acceptLink(href)) {
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
		}
		super.tag(tag);
	}

	// ---------------------------------------------------------------- conditional comments


	@Override
	public void condComment(CharSequence conditionalComment, boolean isStartingTag, boolean isDownlevelHidden) {
		insideConditionalComment = isStartingTag;
		super.condComment(conditionalComment, isStartingTag, isDownlevelHidden);
	}

	// ---------------------------------------------------------------- end

	@Override
	public void end() {
		jsBundleAction.end();
		cssBundleAction.end();
		super.end();
	}

	/**
	 * Post process final content. Required for <code>RESOURCE_ONLY</code> strategy.
	 */
	public char[] postProcess(char[] content) {
		content = jsBundleAction.replaceBundleId(content);
		content = cssBundleAction.replaceBundleId(content);
		return content;
	}
}
