// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto;

import jodd.io.FileUtil;
import jodd.lagarto.dom.Document;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.lagarto.dom.jerry.Jerry;
import jodd.lagarto.dom.jerry.JerryFunction;
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

		html = "<a href=../org/w3c/dom/'http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/#element-list'>xxx</a>";

		lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCalculatePosition(true);
		document = lagartoDOMBuilder.parse(html);

		assertEquals("<a href=\"../org/w3c/dom/&#039;http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/#element-list&#039;\">xxx</a>", document.getHtml());
	}

	public void testIssue23_0() throws IOException {
		File file = new File(testDataRoot, "index-4-v0.html");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCalculatePosition(true);
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(FileUtil.readString(file));

		assertEquals(1, doc.getErrors().size());
	}

	public void testIssue23_1() throws IOException {
		File file = new File(testDataRoot, "index-4-v1.html");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.setCalculatePosition(true);
		lagartoDOMBuilder.setCollectErrors(true);
		Document doc = lagartoDOMBuilder.parse(FileUtil.readString(file));

		System.out.println(doc.getHtml());

		assertEquals(13, doc.getErrors().size());
	}

	public void testIssue23() throws IOException {
		File file = new File(testDataRoot, "index-4.html");

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
//		lagartoDOMBuilder.setCalculatePosition(true);
		lagartoDOMBuilder.setCollectErrors(true);
		Document document = lagartoDOMBuilder.parse(FileUtil.readString(file));

		// 1564 open DTs + 1564 open DDs + 1 open P
		assertEquals(3129, document.getErrors().size());

		Jerry doc = Jerry.jerry(FileUtil.readString(file));
		assertEquals(16, doc.$("td.NavBarCell1").size());

		assertEquals(1, doc.$("dl").size());
		assertEquals(1564, doc.$("dd").size());
		assertEquals(1564, doc.$("dt").size());
	}
}
