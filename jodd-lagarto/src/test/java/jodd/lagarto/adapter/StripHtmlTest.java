// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.adapter;

import jodd.lagarto.LagartoParser;
import jodd.lagarto.TagWriter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StripHtmlTest {

	@Test
	public void testStripHtml() {
		LagartoParser lagartoParser = new LagartoParser(
				"<html>  <div   id='a'>   x \n\n\n </div>  </html>", false);

		StringBuilder out = new StringBuilder();
		StripHtmlTagAdapter stripHtmlTagAdapter = new StripHtmlTagAdapter(new TagWriter(out));

		lagartoParser.parse(stripHtmlTagAdapter);

		assertEquals("<html><div id=\"a\"> x </div></html>", out.toString());
	}
}