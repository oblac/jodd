// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.io.FileUtil;
import jodd.lagarto.dom.Document;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.lagarto.dom.jerry.Jerry;
import jodd.lagarto.dom.jerry.JerryFunction;
import jodd.util.StringUtil;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;

public class ParsingProblemsTest extends TestCase {

	protected String testDataRoot;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (testDataRoot != null) {
			return;
		}
		URL data = LagartoParserTest.class.getResource("data");
		testDataRoot = data.getFile();
	}

	public void testInvalidTag() {
		String html = "<html>text1<=>text2</html>";

		LagartoParser lagartoParser = new LagartoParser(CharBuffer.wrap(html));
		
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
			fail();
		}

		assertEquals("html text1<=>text2 html ", sb.toString());
	}

	public void testNonQuotedAttributeValue() {
		String html = "<a href=123>xxx</a>";

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCalculatePosition(true);
		Document document = lagartoDOMBuilder.parse(html);

		assertEquals("<a href=\"123\">xxx</a>", document.getHtml());
		assertTrue(document.check());

		html = "<a href=../org/w3c/dom/'http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/#element-list'>xxx</a>";

		lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCalculatePosition(true);
		document = lagartoDOMBuilder.parse(html);
		assertTrue(document.check());

		assertEquals("<a href=\"../org/w3c/dom/&#039;http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/#element-list&#039;\">xxx</a>", document.getHtml());
	}

	public void testIssue23_0() throws IOException {
		File file = new File(testDataRoot, "index-4-v0.html");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCalculatePosition(true);
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(FileUtil.readString(file));
		assertTrue(doc.check());

		assertEquals(1, lagartoDOMBuilder.getErrors().size());
	}

	public void testIssue23_1() throws IOException {
		File file = new File(testDataRoot, "index-4-v1.html");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCalculatePosition(true);
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(FileUtil.readString(file));
		assertTrue(doc.check());

		assertEquals(1, lagartoDOMBuilder.getErrors().size());
	}

	public void testIssue23() throws IOException {
		File file = new File(testDataRoot, "index-4.html");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
//		lagartoDOMBuilder.setCalculatePosition(true);
		lagartoDOMBuilder.setCollectErrors(true);
		Document document = lagartoDOMBuilder.parse(FileUtil.readString(file));
		assertTrue(document.check());

		// (1564 open DTs + 1564 open DDs) 1 open P
		assertEquals(1, lagartoDOMBuilder.getErrors().size());

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
		doc.$("td.NavBarCell1").each(new JerryFunction() {
			public boolean onNode(Jerry $this, int index) {
				sb.append("---\n");
				sb.append($this.text().trim());
				sb.append('\n');
				return true;
			}
		});
		String s = sb.toString();
		s = StringUtil.remove(s, ' ');
		s = StringUtil.remove(s, '\r');
		s = StringUtil.remove(s, '\u00A0');
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
}
