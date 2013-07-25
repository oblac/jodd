//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.io.FileUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static jodd.lagarto.dom.LagartoNodeHtmlRenderer.Case.*;
import static org.junit.Assert.assertEquals;

public class LagartoNodeHtmlRendererTest {

	protected String testDataRoot;

	@Before
	public void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = NodeSelectorTest.class.getResource("test");
		testDataRoot = data.getFile();
	}

	@Test
	public void simpleTest() {
		String html = "<html><boDY><div id=\"z\" fooBar=\"aAa\">some Text</div></boDY></html>";
		LagartoDOMBuilder domBuilder = new LagartoDOMBuilder();

		// case insensitive -> lowercase
		Document document = domBuilder.parse(html);
		String htmlOut = document.getHtml();
		assertEquals("<html><body><div id=\"z\" foobar=\"aAa\">some Text</div></body></html>", htmlOut);

		// case sensitive -> raw
		domBuilder.setCaseSensitive(true);
		document = domBuilder.parse(html);
		htmlOut = document.getHtml();
		assertEquals(html, htmlOut);
	}

	@Test
	public void testCases() {
		String html = "<html><boDY><div id=\"z\" fooBar=\"aAa\">some Text</div></boDY></html>";
		LagartoDOMBuilder domBuilder = new LagartoDOMBuilder();

		// case insensitive -> lowercase
		Document document = domBuilder.parse(html);
		LagartoNodeHtmlRenderer renderer = domBuilder.getRenderer();

		// raw, default
		renderer.setTagCase(RAW);
		renderer.setAttributeCase(DEFAULT);
		assertEquals("<html><boDY><div id=\"z\" foobar=\"aAa\">some Text</div></boDY></html>", document.getHtml());

		// raw, raw
		renderer.setTagCase(RAW);
		renderer.setAttributeCase(RAW);
		assertEquals(html, document.getHtml());

		// default, raw
		renderer.setTagCase(DEFAULT);
		renderer.setAttributeCase(RAW);
		assertEquals("<html><body><div id=\"z\" fooBar=\"aAa\">some Text</div></body></html>", document.getHtml());

		// default, default
		renderer.setTagCase(DEFAULT);
		renderer.setAttributeCase(DEFAULT);
		assertEquals("<html><body><div id=\"z\" foobar=\"aAa\">some Text</div></body></html>", document.getHtml());

		// lowercase, uppercase
		renderer.setTagCase(LOWERCASE);
		renderer.setAttributeCase(UPPERCASE);
		assertEquals("<html><body><div ID=\"z\" FOOBAR=\"aAa\">some Text</div></body></html>", document.getHtml());

		// uppercase, lowercase
		renderer.setTagCase(UPPERCASE);
		renderer.setAttributeCase(LOWERCASE);
		assertEquals("<HTML><BODY><DIV id=\"z\" foobar=\"aAa\">some Text</DIV></BODY></HTML>", document.getHtml());
	}

	// ---------------------------------------------------------------- vsethi test

	/**
	 * Custom renderer, example of dynamic rules:
	 *
	 * + HTML tags are lowercase
	 * + NON-HTML tags remains raw
	 * + HTML attr names are lowercase
	 * + NON-HTML attr names are raw
	 * + XML block is detected by xml-attrib attribute
	 * + XML block is all RAW
	 */
	public static class CustomRenderer extends LagartoNodeHtmlRenderer {
		public CustomRenderer() {
			configHtml();
		}

		protected void configHtml() {
			setTagCase(LOWERCASE);
			setAttributeCase(LOWERCASE);
		}
		protected void configXML() {
			setTagCase(RAW);
			setAttributeCase(RAW);
		}

		@Override
		protected String resolveAttributeName(Node node, Attribute attribute) {
			String attributeName = attribute.getRawName();
			if (attributeName.contains("_") || attributeName.contains("-")) {
				return attributeName;
			}
			return super.resolveAttributeName(node, attribute);
		}

		@Override
		public void renderElementBody(Element element, Appendable appendable) throws IOException {
			// detects XML content
			boolean hasXML = element.hasAttribute("xml-attrib");

			if (hasXML) {
				configXML();
			}
			super.renderElementBody(element, appendable);
			if (hasXML) {
				configHtml();
			}
		}
	}

	@Test
	public void testVKSethi() throws IOException {
		String html = FileUtil.readString(new File(testDataRoot, "vksethi.html"));
		String htmlExpected = FileUtil.readString(new File(testDataRoot, "vksethi-out.html"));

		LagartoDOMBuilder domBuilder = new LagartoDOMBuilder();
		for (int i = 0; i < 2; i++) {
			// this does not change anything with html output
			domBuilder.setCaseSensitive(i == 1);

			// case insensitive -> lowercase
			Document document = domBuilder.parse(html);

			// custom renderer
			domBuilder.setRenderer(new CustomRenderer());

			String htmlOut = document.getHtml();
			assertEquals(htmlExpected, htmlOut);
		}
	}

}