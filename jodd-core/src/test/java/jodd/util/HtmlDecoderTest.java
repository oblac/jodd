// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HtmlDecoderTest {

	@Test
	public void testHexDecimal() {
		String s;
		s = HtmlDecoder.decode("&#xFF;");
		assertEquals(1, s.length());
		assertEquals(0xFF, s.charAt(0));

		s = HtmlDecoder.decode("&#xFF");
		assertEquals(5, s.length());
		assertEquals("&#xFF", s);

		s = HtmlDecoder.decode("&#");
		assertEquals("&#", s);

		s = HtmlDecoder.decode("&");
		assertEquals("&", s);

		s = HtmlDecoder.decode("&#123");
		assertEquals("&#123", s);

		s = HtmlDecoder.decode("&#123;");
		assertEquals(1, s.length());
		assertEquals(123, s.charAt(0));

		s = HtmlDecoder.decode("aaa &#x41;&#65; aaa");
		assertEquals("aaa AA aaa", s);
	}

	@Test
	public void testEntities() {
		String s;

		s = HtmlDecoder.decode("&amp;");
		assertEquals("&", s);

		s = HtmlDecoder.decode("&amp");
		assertEquals("&amp", s);

		s = HtmlDecoder.decode("&");
		assertEquals("&", s);

		s = HtmlDecoder.decode("2 &lt; 5");
		assertEquals("2 < 5", s);

		s = HtmlDecoder.decode("&aacute;");
		assertEquals(1, s.length());
		assertEquals(0xe1, s.charAt(0));
	}

	@Test
	public void testSameNames() {
		String s;

		s = HtmlDecoder.decode("&aacute;");
		assertEquals(1, s.length());
		assertEquals(0xE1, s.charAt(0));

		s = HtmlDecoder.decode("&Aacute;");
		assertEquals(1, s.length());
		assertEquals(0xC1, s.charAt(0));
	}

	@Test
	public void testDecodeNotFound() {
		String s = "switchTab(&quot;Senthil1&quot;);showWorkFlow(&quot;/xyz/abc.jsp?strWorkId=1691&archived=0&quot;);";

		String out = HtmlDecoder.decode(s);

		assertEquals("switchTab(\"Senthil1\");showWorkFlow(\"/xyz/abc.jsp?strWorkId=1691&archived=0\");", out);
	}

	@Test
	public void testEmitTwoChars() {
		String s = "Hey&acE;!";

		String out = HtmlDecoder.decode(s);

		assertEquals("Hey\u223E\u0333!", out);
	}

	@Test
	public void testDetectName() {
		char[] str = "&nbsp;".toCharArray();
		assertEquals("nbsp", HtmlDecoder.detectName(str, 1));

		str = "&nbsppppp".toCharArray();
		assertEquals("nbsp", HtmlDecoder.detectName(str, 1));

		str = "&nb".toCharArray();
		assertEquals(null, HtmlDecoder.detectName(str, 1));

		str = "&acE".toCharArray();
		assertEquals("acE", HtmlDecoder.detectName(str, 1));

		str = "&notit".toCharArray();
		assertEquals("not", HtmlDecoder.detectName(str, 1));
	}
}