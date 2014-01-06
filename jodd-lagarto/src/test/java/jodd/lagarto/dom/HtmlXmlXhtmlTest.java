// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
