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

package jodd.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
