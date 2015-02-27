// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.htmlstapler;

import jodd.lagarto.Tag;
import jodd.lagarto.TagAdapter;
import jodd.lagarto.TagUtil;
import jodd.lagarto.TagVisitor;
import jodd.util.Util;

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
			String src = Util.toString(tag.getAttributeValue("src"));

			if (src == null) {
				super.script(tag, body);
				return;
			}

			if (jsBundleAction.acceptLink(src)) {
				String link = jsBundleAction.processLink(src);
				if (link != null) {
					tag.setAttributeValue("src", link);
					super.script(tag, body);
				}
				return;
			}
		}
		super.script(tag, body);
	}

	// ---------------------------------------------------------------- css

	private static final char[] T_LINK = new char[] {'l', 'i', 'n', 'k'};

	@Override
	public void tag(Tag tag) {
		if (insideConditionalComment == false) {
			if (tag.nameEquals(T_LINK)) {
				CharSequence type = tag.getAttributeValue("type");

				if (type != null && TagUtil.equalsIgnoreCase(type, "text/css") == true) {
					String media = Util.toString(tag.getAttributeValue("media"));

					if (media == null || media.contains("screen")) {
						String href = Util.toString(tag.getAttributeValue("href"));

						if (cssBundleAction.acceptLink(href)) {
							String link = cssBundleAction.processLink(href);
							if (link != null) {
								tag.setAttribute("href", link);
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
	public void condComment(CharSequence expression, boolean isStartingTag, boolean isHidden, boolean isHiddenEndTag) {
		insideConditionalComment = isStartingTag;
		super.condComment(expression, isStartingTag, isHidden, isHiddenEndTag);
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
