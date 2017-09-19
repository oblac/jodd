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

import jodd.io.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static jodd.lagarto.dom.LagartoHtmlRendererNodeVisitor.Case.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LagartoHtmlRendererTest {

	protected String testDataRoot;

	@BeforeEach
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
		domBuilder.getConfig().setCaseSensitive(true);
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

		// raw, default

		document.getConfig().setLagartoHtmlRenderer(
			new LagartoHtmlRenderer() {
				@Override
				protected NodeVisitor createRenderer(Appendable appendable) {
					LagartoHtmlRendererNodeVisitor renderer =
							(LagartoHtmlRendererNodeVisitor) super.createRenderer(appendable);

					renderer.setTagCase(RAW);
					renderer.setAttributeCase(DEFAULT);

					return renderer;
				}
			}
		);

		assertEquals("<html><boDY><div id=\"z\" foobar=\"aAa\">some Text</div></boDY></html>", document.getHtml());

		// raw, raw
		document.getConfig().setLagartoHtmlRenderer(
			new LagartoHtmlRenderer() {
				@Override
				protected NodeVisitor createRenderer(Appendable appendable) {
					LagartoHtmlRendererNodeVisitor renderer =
							(LagartoHtmlRendererNodeVisitor) super.createRenderer(appendable);

					renderer.setTagCase(RAW);
					renderer.setAttributeCase(RAW);

					return renderer;
				}
			}
		);

		assertEquals(html, document.getHtml());

		// default, raw

		document.getConfig().setLagartoHtmlRenderer(
			new LagartoHtmlRenderer() {
				@Override
				protected NodeVisitor createRenderer(Appendable appendable) {
					LagartoHtmlRendererNodeVisitor renderer =
							(LagartoHtmlRendererNodeVisitor) super.createRenderer(appendable);

					renderer.setTagCase(DEFAULT);
					renderer.setAttributeCase(RAW);

					return renderer;
				}
			}
		);

		assertEquals("<html><body><div id=\"z\" fooBar=\"aAa\">some Text</div></body></html>", document.getHtml());

		// default, default
		document.getConfig().setLagartoHtmlRenderer(
			new LagartoHtmlRenderer() {
				@Override
				protected NodeVisitor createRenderer(Appendable appendable) {
					LagartoHtmlRendererNodeVisitor renderer =
							(LagartoHtmlRendererNodeVisitor) super.createRenderer(appendable);

					renderer.setTagCase(DEFAULT);
					renderer.setAttributeCase(DEFAULT);

					return renderer;
				}
			}
		);

		assertEquals("<html><body><div id=\"z\" foobar=\"aAa\">some Text</div></body></html>", document.getHtml());

		// lowercase, uppercase
		document.getConfig().setLagartoHtmlRenderer(
			new LagartoHtmlRenderer() {
				@Override
				protected NodeVisitor createRenderer(Appendable appendable) {
					LagartoHtmlRendererNodeVisitor renderer =
							(LagartoHtmlRendererNodeVisitor) super.createRenderer(appendable);

					renderer.setTagCase(LOWERCASE);
					renderer.setAttributeCase(UPPERCASE);

					return renderer;
				}
			}
		);

		assertEquals("<html><body><div ID=\"z\" FOOBAR=\"aAa\">some Text</div></body></html>", document.getHtml());

		// uppercase, lowercase
		// lowercase, uppercase
		document.getConfig().setLagartoHtmlRenderer(
				new LagartoHtmlRenderer() {
					@Override
					protected NodeVisitor createRenderer(Appendable appendable) {
						LagartoHtmlRendererNodeVisitor renderer =
								(LagartoHtmlRendererNodeVisitor) super.createRenderer(appendable);

						renderer.setTagCase(UPPERCASE);
						renderer.setAttributeCase(LOWERCASE);

						return renderer;
					}
				}
		);

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
	public static class CustomRenderer extends LagartoHtmlRenderer {
		@Override
		protected NodeVisitor createRenderer(Appendable appendable) {

			return new LagartoHtmlRendererNodeVisitor(appendable) {

				@Override
				public void document(Document document) {
					configHtml();
					super.document(document);
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
				protected void elementBody(Element element) throws IOException {
					boolean hasXML = element.hasAttribute("xml-attrib");

					// detects XML content
					if (hasXML) {
						configXML();
					}

					super.elementBody(element);

					if (hasXML) {
						configHtml();
					}

				}

			};
		}
	}

	@Test
	public void testVKSethi() throws IOException {
		String html = FileUtil.readString(new File(testDataRoot, "vksethi.html"));
		String htmlExpected = FileUtil.readString(new File(testDataRoot, "vksethi-out.html"));

		LagartoDOMBuilder domBuilder = new LagartoDOMBuilder();
		for (int i = 0; i < 2; i++) {
			// this does not change anything with html output
			domBuilder.getConfig().setCaseSensitive(i == 1);
			domBuilder.getConfig().setLagartoHtmlRenderer(new CustomRenderer());

			Document document = domBuilder.parse(html);

			String htmlOut = document.getHtml();
			assertEquals(htmlExpected, htmlOut);
		}
	}

}
