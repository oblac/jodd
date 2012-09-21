// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import junit.framework.TestCase;

public class MalformedTest extends TestCase {

	public void testOneNode() {
		String content = "<body><div>test<span>sss</span></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span>sss</span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	public void testOneNodeWithBlanks() {
		String content = "<body><div>   <span>sss</span></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>   <span>sss</span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	public void testTwoNodes() {
		String content = "<body><div>test<span><form>xxx</form></body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span><form>xxx</form></span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	public void testTwoNodes2() {
		String content = "<body><div>test<span><form>xxx</body>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test<span><form>xxx</form></span></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	public void testPeterSimple1() {
		String content = "<div><h1>FORELE</h1><p>dicuss<div>xxx</div></div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><h1>FORELE</h1><p>dicuss</p><div>xxx</div></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	public void testPeterSimple2() {
		String content = "<div><h1>FORELE</h1><p>dicuss<div><h2>HAB</h2><p>AMONG</div></div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div><h1>FORELE</h1><p>dicuss</p><div><h2>HAB</h2><p>AMONG</p></div></div>", doc.getHtml());
		assertTrue(doc.check());
	}

	public void testPeterSimple3WithSpaces() {
		String content = "<div> <h1>FORELE</h1> <p>dicuss <div> <h2>HAB</h2> <p>AMONG </div> </div>".toUpperCase();
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<div> <h1>FORELE</h1> <p>DICUSS </p><div> <h2>HAB</h2> <p>AMONG </p></div> </div>", doc.getHtml());
		assertTrue(doc.check());
	}

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

	public void testEof() {
		String content = "<body><div>test";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div>test</div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

	public void testEof2() {
		String content = "<body><div>";
		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		Document doc = lagartoDOMBuilder.parse(content);
		assertEquals("<body><div></div></body>", doc.getHtml());
		assertTrue(doc.check());
	}

}
