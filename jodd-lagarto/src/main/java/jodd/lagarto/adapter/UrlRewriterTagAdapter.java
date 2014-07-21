// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter;

import jodd.lagarto.Tag;
import jodd.lagarto.TagAdapter;
import jodd.lagarto.TagVisitor;
import jodd.util.CharUtil;

/**
 * URL Rewriter tag adapter.
 *
 * todo add more util methods for easier URL parsing
 */
public abstract class UrlRewriterTagAdapter extends TagAdapter {

	public UrlRewriterTagAdapter(TagVisitor target) {
		super(target);
	}

	@Override
	public void tag(Tag tag) {
		if (tag.getType().isStartingTag()) {
			CharSequence tagName = tag.getName();

			if (tagName.length() == 1 && CharUtil.toLowerAscii(tagName.charAt(0)) == 'a') {
				CharSequence href = tag.getAttributeValue("href");

				if (href != null) {
					CharSequence newHref = rewriteUrl(href);

					if (newHref != href) {
						tag.setAttribute("href", newHref);
					}
				}
			}
		}
		super.tag(tag);
	}

	/**
	 * Rewrites URLs. Returns input value when no rewriting is needed.
	 */
	protected abstract CharSequence rewriteUrl(CharSequence url);

}