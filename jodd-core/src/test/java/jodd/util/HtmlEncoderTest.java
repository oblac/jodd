// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlEncoderTest {

	@Test
	public void testEncodeText() {
		String html = "< & \" ' > \r\n \n  \t";

		assertEquals("&lt; &amp; \" ' &gt; \r\n \n  \t", HtmlEncoder.text(html));
		assertEquals("&lt; &amp; &quot; &#39; &gt; \r\n \n  \t", HtmlEncoder.xml(html));

		html = "";
		assertEquals("", HtmlEncoder.text(html));

		assertEquals("", HtmlEncoder.text(null));
	}

	@Test
	public void testEncodeAttribute() {
		String html = "< & \" ' > \r\n \n  \t";

		assertEquals("< &amp; &quot; ' > \r\n \n  \t", HtmlEncoder.attributeDoubleQuoted(html));
		assertEquals("< &amp; \" &#39; > \r\n \n  \t", HtmlEncoder.attributeSingleQuoted(html));
	}

	@Test
	public void testNbsp() {
		assertEquals(" ", HtmlEncoder.text(" "));
		assertEquals("&nbsp;", HtmlEncoder.text("\u00a0"));
		assertEquals("\u00a0", HtmlDecoder.decode("&nbsp;"));
	}

}