// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlEncoderTest {

	@Test
	public void testEncode() {
		String html = "< & \" ' > \r\n \n  \t";

		assertEquals("&lt; &amp; \" ' &gt; \r\n \n  \t", HtmlEncoder.text(html));
		assertEquals("&lt; &amp; &quot; ' &gt; \r\n \n  \t", HtmlEncoder.attribute(html));
		assertEquals("&lt; &amp; &quot; &#39; &gt; \r\n \n  \t", HtmlEncoder.xml(html));

		html = "";
		assertEquals("", HtmlEncoder.text(html));

		html = null;
		assertEquals("", HtmlEncoder.text(html));

		html = new String(new char[]{128, 257});

		try {
			HtmlEncoder.text(html);
			Assert.fail();
		} catch (Exception ex) {
		}
	}

	@Test
	public void testNbsp() {
		assertEquals(" ", HtmlEncoder.text(" "));
		assertEquals("&nbsp;", HtmlEncoder.text("\u00a0"));
		assertEquals("\u00a0", HtmlDecoder.decode("&nbsp;"));
	}

}