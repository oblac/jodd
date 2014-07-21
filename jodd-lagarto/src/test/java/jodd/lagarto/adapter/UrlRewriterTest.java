// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter;

import jodd.lagarto.LagartoParser;
import jodd.lagarto.TagWriter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlRewriterTest {

	@Test
	public void testUrlRewriter() {
		LagartoParser lagartoParser = new LagartoParser(
				"<a href=\"http://jodd.org\">1</a><a href=\"page.html\">2</a>", false);

		StringBuilder out = new StringBuilder();
		UrlRewriterTagAdapter urlRewriterTagAdapter = new UrlRewriterTagAdapter(new TagWriter(out)) {

			@Override
			protected CharSequence rewriteUrl(CharSequence url) {
				String u = url.toString();
				if (u.startsWith("http")) {
					return url;
				}

				return "/ctx/" + url;
			}
		};

		lagartoParser.parse(urlRewriterTagAdapter);

		assertEquals(
				"<a href=\"http://jodd.org\">1</a><a href=\"/ctx/page.html\">2</a>", out.toString());
	}

}