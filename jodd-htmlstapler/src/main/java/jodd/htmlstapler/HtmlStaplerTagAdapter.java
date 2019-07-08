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

import jodd.lagarto.Tag;
import jodd.lagarto.TagAdapter;
import jodd.lagarto.TagVisitor;
import jodd.util.CharArraySequence;
import jodd.util.CharSequenceUtil;
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

	public HtmlStaplerTagAdapter(final HtmlStaplerBundlesManager bundlesManager, final String servletPath, final TagVisitor target) {
		super(target);

		this.bundlesManager = bundlesManager;

		jsBundleAction = bundlesManager.start(servletPath, "js");
		cssBundleAction = bundlesManager.start(servletPath, "css");

		insideConditionalComment = false;
	}

	// ---------------------------------------------------------------- javascripts

	@Override
	public void script(final Tag tag, final CharSequence body) {
		if (!insideConditionalComment) {
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

	private static final CharSequence T_LINK = CharArraySequence.of('l', 'i', 'n', 'k');

	@Override
	public void tag(final Tag tag) {
		if (!insideConditionalComment) {
			if (tag.nameEquals(T_LINK)) {
				CharSequence type = tag.getAttributeValue("type");

				if (type != null && CharSequenceUtil.equalsIgnoreCase(type, "text/css")) {
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
	public void condComment(final CharSequence expression, final boolean isStartingTag, final boolean isHidden, final boolean isHiddenEndTag) {
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
