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

		html = null;
		assertEquals("", HtmlEncoder.block(html));

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
	public void testEncodeTextStrict() {
		String html = "< & \" ' > \r\n \n  \t";

		assertEquals("&lt; &amp; &quot; &#039; &gt; <br/> <br/> &nbsp;\t", HtmlEncoder.strict(html));

		html = "";
		assertEquals("", HtmlEncoder.strict(html));

		html = null;
		assertEquals("", HtmlEncoder.strict(html));

		html = new String(new char[]{128, 257});
		assertEquals(html, HtmlEncoder.strict(html));

		html = "\r\n\n\r";
		assertEquals("<br/><br/><br/>", HtmlEncoder.strict(html));

		html = "\r\n\r\n";
		assertEquals("<br/><br/>", HtmlEncoder.strict(html));

		html = "\n\r";
		assertEquals("<br/><br/>", HtmlEncoder.strict(html));


		html = " ";
		assertEquals(" ", HtmlEncoder.strict(html));
		html = "  ";
		assertEquals(" &nbsp;", HtmlEncoder.strict(html));
		html = "   ";
		assertEquals(" &nbsp; ", HtmlEncoder.strict(html));
		html = "    ";
		assertEquals(" &nbsp; &nbsp;", HtmlEncoder.strict(html));
		html = "     ";
		assertEquals(" &nbsp; &nbsp; ", HtmlEncoder.strict(html));

		html = " a";
		assertEquals(" a", HtmlEncoder.strict(html));
		html = "  a";
		assertEquals(" &nbsp;a", HtmlEncoder.strict(html));
		html = "   a";
		assertEquals(" &nbsp; a", HtmlEncoder.strict(html));
		html = "    a";
		assertEquals(" &nbsp; &nbsp;a", HtmlEncoder.strict(html));
		html = "     a";
		assertEquals(" &nbsp; &nbsp; a", HtmlEncoder.strict(html));

		html = "a ";
		assertEquals("a ", HtmlEncoder.strict(html));
		html = "a  ";
		assertEquals("a &nbsp;", HtmlEncoder.strict(html));
		html = "a   ";
		assertEquals("a &nbsp; ", HtmlEncoder.strict(html));
		html = "a    ";
		assertEquals("a &nbsp; &nbsp;", HtmlEncoder.strict(html));
		html = "a     ";
		assertEquals("a &nbsp; &nbsp; ", HtmlEncoder.strict(html));

		html = " a ";
		assertEquals(" a ", HtmlEncoder.strict(html));
		html = "  a  ";
		assertEquals(" &nbsp;a &nbsp;", HtmlEncoder.strict(html));
		html = " a  ";
		assertEquals(" a &nbsp;", HtmlEncoder.strict(html));
		html = "  a ";
		assertEquals(" &nbsp;a ", HtmlEncoder.strict(html));
		html = "  a b   c  d e";
		assertEquals(" &nbsp;a b &nbsp; c &nbsp;d e", HtmlEncoder.strict(html));
	}

	@Test
	public void testNbsp() {
		assertEquals(" ", HtmlEncoder.text(" "));
		assertEquals("\u00a0", HtmlEncoder.text("\u00a0"));
		assertEquals("\u00a0", HtmlDecoder.decode("&nbsp;"));
	}

}