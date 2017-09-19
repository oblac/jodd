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

package jodd.lagarto.dom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlXmlXhtmlTest {

	private static final String HTML_STRING = "<html><meta><body><div>xxx<br>zzz</div><span></span></body></html>";
	private static final String XHTML_STRING = "<html><meta/><body><div>xxx<br/>zzz</div><span></span></body></html>";
	private static final String XML_STRING = "<html><meta></meta><body><div>xxx<br></br>zzz</div><span></span></body></html>";

	@Test
	public void testHtml2Html() {
		String content = HTML_STRING;
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.enableHtmlMode().parse(content);
		assertEquals(HTML_STRING, doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testHtml2XHtml() {
		String content = HTML_STRING;
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.enableXhtmlMode().parse(content);
		assertEquals(XHTML_STRING, doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testXHtml2Html() {
		String content = XHTML_STRING;
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.enableHtmlMode().parse(content);
		assertEquals(HTML_STRING, doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testXml() {
		String content = XML_STRING;
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.enableXmlMode().parse(content);
		assertEquals(XML_STRING, doc.getHtml());
		assertTrue(doc.check());
	}

}
