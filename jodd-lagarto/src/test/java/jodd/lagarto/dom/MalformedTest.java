// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.util.StringUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MalformedTest {

	@Test
	public void testOneNode() {
		String content = "<body><div>test<span>sss</span></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span>sss</span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testOneNodeWithBlanks() {
		String content = "<body><div>   <span>sss</span></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>   <span>sss</span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testTwoNodes() {
		String content = "<body><div>test<span><form>xxx</form></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span><form>xxx</form></span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testTwoNodes2() {
		String content = "<body><div>test<span><form>xxx</body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span><form>xxx</form></span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterSimple1() {
		String content = "<div><h1>FORELE</h1><p>dicuss<div>xxx</div></div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><h1>FORELE</h1><p>dicuss</p><div>xxx</div></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterSimple2() {
		String content = "<div><h1>FORELE</h1><p>dicuss<div><h2>HAB</h2><p>AMONG</div></div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><h1>FORELE</h1><p>dicuss</p><div><h2>HAB</h2><p>AMONG</p></div></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterSimple3WithSpaces() {
		String content = "<div> <h1>FORELE</h1> <p>dicuss <div> <h2>HAB</h2> <p>AMONG </div> </div>".toUpperCase();
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div> <h1>FORELE</h1> <p>DICUSS </p><div> <h2>HAB</h2> <p>AMONG </p></div> </div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testPeterFull() {
		String content = "<DIV class=\"section\" id=\"forest-elephants\" >\n" +
				"<H1>Forest elephants</H1>\n" +
				"<P>In this section, we discuss the lesser known forest elephants.\n" +
				"...this section continues...\n" +
				"<DIV class=\"subsection\" id=\"forest-habitat\" >\n" +
				"<H2>Habitat</H2>\n" +
				"<P>Forest elephants do not live in trees but among them.\n" +
				"...this subsection continues...\n" +
				"</DIV>\n" +
				"</DIV>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);

		String expected = "<div class=\"section\" id=\"forest-elephants\">\n" +
				"<h1>Forest elephants</h1>\n" +
				"<p>In this section, we discuss the lesser known forest elephants.\n" +
				"...this section continues...\n</p>" +
				"<div class=\"subsection\" id=\"forest-habitat\">\n" +
				"<h2>Habitat</h2>\n" +
				"<p>Forest elephants do not live in trees but among them.\n" +
				"...this subsection continues...\n</p>" +
				"</div>\n" +
				"</div>";

		assertEquals(expected, doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testEof() {
		String content = "<body><div>test";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test</div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testEof2() {
		String content = "<body><div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testSpanDivOverTable() {
		String content = "<span><div><table><tr><td>text</span>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<span><div><table><tr><td>text</td></tr></table></div></span>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testDivSpanOverTable() {
		String content = "<div><span><table><tr><td>text</div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><span><table><tr><td>text</td></tr></table></span></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	@Test
	public void testTableInTableInTable() {
		String html =
				"<table>" +
				"    <tr>" +
				"        <td>111</td>" +
				"    </tr>" +
				"    <tr>" +
				"        <td>" +
				"            <table>" +
				"                <tr>" +
				"                    <td>222" +
				"                        <table>" +
				"                            <tr>" +
				"                                <td>333</td>" +
				"                            </td>" +
				"                        </table>" +
				"            </table>" +
				"</table>";
		html = StringUtil.removeChars(html, ' ');

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(html);
		String out =
				"<table>" +
				"    <tr>" +
				"        <td>111</td>" +
				"    </tr>" +
				"    <tr>" +
				"        <td>" +
				"            <table>" +
				"                <tr>" +
				"                    <td>222" +
				"                        <table>" +
				"                            <tr>" +
				"                                <td>333</td>" +
				"                            </tr>" +
				"                        </table>" +
				"                    </td>" +
				"                </tr>" +
				"            </table>" +
				"        </td>" +
				"    </tr>" +
				"</table>";

		out = StringUtil.removeChars(out, ' ');
		assertEquals(out, doc.getHtml());
		assertTrue(doc.check());

	}

}
