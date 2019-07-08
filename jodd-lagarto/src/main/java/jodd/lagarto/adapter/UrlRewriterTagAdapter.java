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

	public UrlRewriterTagAdapter(final TagVisitor target) {
		super(target);
	}

	@Override
	public void tag(final Tag tag) {
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