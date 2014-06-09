// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlEncoderTest {

	@Test
	public void testEncode() {
		String html = "< & \" ' > \r\n \n  \t";

		assertEquals("&lt; &amp; &quot; &#039; &gt; \r\n \n  \t", HtmlEncoder.text(html));

		html = "";
		assertEquals("", HtmlEncoder.text(html));

		html = null;
		assertEquals("", HtmlEncoder.text(html));

		html = new String(new char[]{128, 257});
		assertEquals(html, HtmlEncoder.text(html));
	}

	@Test
	public void testEncodeText() {
		String html = "< & \" ' > \r\n \n  \t";

		assertEquals("&lt; &amp; &quot; &#039; &gt; <br/> <br/>  \t", HtmlEncoder.block(html));

		html = "";
		assertEquals("", HtmlEncoder.block(html));

		assertEquals("", HtmlEncoder.block(null));

		html = new String(new char[]{128, 257});
		assertEquals(html, HtmlEncoder.block(html));

		html = "\r\n\n\r";
		assertEquals("<br/><br/><br/>", HtmlEncoder.block(html));

		html = "\r\n\r\n";
		assertEquals("<br/><br/>", HtmlEncoder.block(html));

		html = "\n\r";
		assertEquals("<br/><br/>", HtmlEncoder.block(html));
	}

	@Test
	public void testNbsp() {
		assertEquals(" ", HtmlEncoder.text(" "));
		assertEquals("&nbsp;", HtmlEncoder.text("\u00a0"));
		assertEquals("\u00a0", HtmlDecoder.decode("&nbsp;"));
	}

}