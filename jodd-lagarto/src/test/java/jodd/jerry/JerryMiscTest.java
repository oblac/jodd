// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jerry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JerryMiscTest {

	@Test
	public void testTextContentDecoding() {
		String html = "<html><body><div>&#1054;&#1076;&#1112;&#1072;&#1074;&#1080; &#1089;&#1077;</div></body></html>";

		Jerry doc = Jerry.jerry(html);
		Jerry div = doc.$("div");

		assertEquals(9, div.text().length());
		assertEquals("Одјави се", div.text());
		assertEquals(57, div.html().length());
	}

	@Test
	public void testTextContentDecoding2() {
		String html = "<html><body><div></div></body></html>";

		Jerry doc = Jerry.jerry(html);
		Jerry div = doc.$("div");
		assertEquals(0, div.text().length());
		div.text("Одјави се");
		assertEquals(9, div.text().length());
		assertEquals("Одјави се", div.text());
		assertEquals(9, div.html().length());
	}

	@Test
	public void testAppend1() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		jerryParser.enableHtmlMode();

		Jerry doc = jerryParser.parse("<xml><book><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book><name>Foo</name><br></book></xml>", doc.html());
	}

	@Test
	public void testAppend2() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		jerryParser.enableXmlMode();

		Jerry doc = jerryParser.parse("<xml><book><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book><name>Foo</name><br></br></book></xml>", doc.html());
	}

	@Test
	public void testAppend3() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		jerryParser.enableXhtmlMode();

		Jerry doc = jerryParser.parse("<xml><book><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book><name>Foo</name><br/></book></xml>", doc.html());
	}

	@Test
	public void testNullForEmpty() {
		Jerry doc = Jerry.jerry().parse("<html></html>");

		assertNull(doc.$("#not-a-valid-id").attr("someAttribute"));

		assertNull(doc.$("#not-a-valid-id").css("name"));

		assertNull(doc.$("#not-a-valid-id").html());
	}
}
