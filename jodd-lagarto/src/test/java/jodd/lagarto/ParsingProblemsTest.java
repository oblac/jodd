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

package jodd.lagarto;

import jodd.io.FileUtil;
import jodd.lagarto.dom.Element;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.jerry.Jerry;
import jodd.lagarto.dom.Document;
import jodd.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class ParsingProblemsTest {

	protected String testDataRoot;

	@BeforeEach
	public void setUp() throws Exception {
		if (testDataRoot != null) {
			return;
		}
		URL data = LagartoParserTest.class.getResource("data");
		testDataRoot = data.getFile();
	}

	@Test
	public void testInvalidTag() {
		String html = "<html>text1<=>text2</html>";

		LagartoParser lagartoParser = new LagartoParser(html, false);

		final StringBuilder sb = new StringBuilder();

		try {
			lagartoParser.parse(new EmptyTagVisitor() {
				@Override
				public void tag(Tag tag) {
					sb.append(tag.getName()).append(' ');
				}

				@Override
				public void text(CharSequence text) {
					sb.append(text).append(' ');
				}

				@Override
				public void error(String message) {
					System.out.println(message);
				}
			});
		} catch (LagartoException lex) {
			lex.printStackTrace();
			fail("error");
		}

		assertEquals("html text1 <=>text2 html ", sb.toString());
	}

	@Test
	public void testNonQuotedAttributeValue() {
		String html = "<a href=123>xxx</a>";

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.getConfig().setCalculatePosition(true);
		Document document = lagartoDOMBuilder.parse(html);

		assertEquals("<a href=\"123\">xxx</a>", document.getHtml());
		assertTrue(document.check());

		html = "<a href=../org/w3c/dom/'http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/#element-list'>xxx</a>";

		lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.getConfig().setCalculatePosition(true);
		document = lagartoDOMBuilder.parse(html);
		assertTrue(document.check());

		assertEquals("<a href=\"../org/w3c/dom/'http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/#element-list'\">xxx</a>", document.getHtml());
	}

	@Test
	public void testIssue23_0() throws IOException {
		File file = new File(testDataRoot, "index-4-v0.html");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.getConfig().setCalculatePosition(true);
		lagartoDOMBuilder.getConfig().setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(FileUtil.readString(file));
		assertTrue(doc.check());

		assertEquals(1, doc.getErrors().size());
	}

	@Test
	public void testIssue23_1() throws IOException {
		File file = new File(testDataRoot, "index-4-v1.html");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.getConfig().setCalculatePosition(true);
		lagartoDOMBuilder.getConfig().setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(FileUtil.readString(file));
		assertTrue(doc.check());

		assertEquals(1, doc.getErrors().size());
	}

	@Test
	public void testIssue23() throws IOException {
		File file = new File(testDataRoot, "index-4.html");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.getConfig().setCalculatePosition(true);
		lagartoDOMBuilder.getConfig().setCollectErrors(true);
		Document document = lagartoDOMBuilder.parse(FileUtil.readString(file));
		assertTrue(document.check());

		// (1564 open DTs + 1564 open DDs) 1 open P
		assertEquals(19, document.getErrors().size());

		Jerry doc = Jerry.jerry(FileUtil.readString(file));
		assertEquals(16, doc.$("td.NavBarCell1").size());
		assertEquals(2, doc.$("table td.NavBarCell1Rev").size());

		assertEquals(1, doc.$("dl").size());
		assertEquals(1564, doc.$("dd").size());
		assertEquals(1564, doc.$("dt").size());
		assertEquals(3144, doc.$("dt a").size());

		// http://docs.oracle.com/javase/6/docs/api/index-files/index-4.html
		file = new File(testDataRoot, "index-4-eng.html");
		doc = Jerry.jerry(FileUtil.readString(file));

		assertEquals(16, doc.$("td.NavBarCell1").size());
		assertEquals(2, doc.$("table td.NavBarCell1Rev").size());

		final StringBuilder sb = new StringBuilder();
		doc.$("td.NavBarCell1").each(($this, index) -> {
			sb.append("---\n");
			sb.append($this.text().trim());
			sb.append('\n');
			return true;
		});
		String s = sb.toString();
		s = StringUtil.remove(s, ' ');
		s = StringUtil.remove(s, '\r');
		s = StringUtil.remove(s, '\u00A0');
		s = StringUtil.remove(s, "&nbsp;");
		assertEquals(
				"---\n" +
						"Overview\n" +
						"Package\n" +
						"Class\n" +
						"Use\n" +
						"Tree\n" +
						"Deprecated\n" +
						"Index\n" +
						"Help\n" +
						"---\n" +
						"Overview\n" +
						"---\n" +
						"Package\n" +
						"---\n" +
						"Class\n" +
						"---\n" +
						"Use\n" +
						"---\n" +
						"Tree\n" +
						"---\n" +
						"Deprecated\n" +
						"---\n" +
						"Help\n" +
						"---\n" +
						"Overview\n" +
						"Package\n" +
						"Class\n" +
						"Use\n" +
						"Tree\n" +
						"Deprecated\n" +
						"Index\n" +
						"Help\n" +
						"---\n" +
						"Overview\n" +
						"---\n" +
						"Package\n" +
						"---\n" +
						"Class\n" +
						"---\n" +
						"Use\n" +
						"---\n" +
						"Tree\n" +
						"---\n" +
						"Deprecated\n" +
						"---\n" +
						"Help\n",
				s);
	}

	@Test
	public void testNamespaces() throws IOException {
		File file = new File(testDataRoot, "namespace.xml");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableXmlMode();
		lagartoDOMBuilder.getConfig().setCalculatePosition(true);

		Document doc = lagartoDOMBuilder.parse(FileUtil.readString(file));
		assertTrue(doc.check());

		Element cfgTestElement = (Element) doc.getChild(1);

		assertEquals("cfg:test", cfgTestElement.getNodeName());

		Element cfgNode = (Element) cfgTestElement.getChild(0);

		assertEquals("cfg:node", cfgNode.getNodeName());



		Jerry.JerryParser jerryParser = new Jerry.JerryParser();

		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableXmlMode();

		Jerry jerry = jerryParser.parse(FileUtil.readString(file));

		final StringBuilder result = new StringBuilder();

		jerry.$("cfg\\:test").each(($this, index) -> {
			result.append($this.$("cfg\\:node").text());
			return true;
		});

		assertEquals("This is a text", result.toString());
	}

	@Test
	public void testPreserveCC() throws IOException {
		File file = new File(testDataRoot, "preserve-cc.html");

		String expectedResult = FileUtil.readString(file);

		Jerry.JerryParser jerryParser = new Jerry.JerryParser();
		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableHtmlMode();
		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).getConfig().setEnableConditionalComments(false);

		Jerry jerry = jerryParser.parse(expectedResult);
		String result = jerry.html();

		assertEquals(expectedResult, result);
	}

	@Test
	public void testKelkoo() throws Exception {
		File file = new File(testDataRoot, "kelkoo.html");
		Jerry jerry;
		try {
			jerry = Jerry.jerry().parse(FileUtil.readString(file));
		} catch (Exception ex) {
			fail(ex.toString());
			throw ex;
		}

		Element script = (Element) jerry.$("script").get(0);

		assertEquals("script", script.getNodeName());
		assertEquals(6, script.getAttributesCount());

		assertEquals("src", script.getAttribute(0).getName());
		assertEquals("data-config", script.getAttribute(1).getName());
		assertEquals("ext\\u00e9rieur|barbecue,", script.getAttribute(2).getName());
		assertEquals("planchaaccessoires\":\"http:\\", script.getAttribute(3).getName());
		assertEquals("www.kelkoo.fr\"}'", script.getAttribute(4).getName());
		assertEquals("data-adsense-append", script.getAttribute(5).getName());
	}

	@Test
	public void testEntity() throws Exception {
		assertEquals(
			"<head><title>Peanut Butter &amp; Jelly</title>" +
				"it's yummy &amp; delicious</head>",
			Jerry.jerry().parse(
				"<head><title>Peanut Butter & Jelly</title>" +
					"it's yummy & delicious").html());
	}

}
