// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jerry;

import jodd.lagarto.dom.Element;
import jodd.lagarto.dom.LagartoDOMBuilder;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

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

		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableHtmlMode();

		Jerry doc = jerryParser.parse("<xml><book><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book><name>Foo</name><br></book></xml>", doc.html());
	}

	@Test
	public void testAppend2() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableXmlMode();

		Jerry doc = jerryParser.parse("<xml><book><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book><name>Foo</name><br></br></book></xml>", doc.html());
	}

	@Test
	public void testAppend3() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableXhtmlMode();

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

	@Test
	public void testFirstNotDirectly() {
		Jerry doc = Jerry.jerry().parse("<html><div>one</div><p>two</p><div>three</div><p>four</p></html>");

		assertEquals(2, doc.$("div").size());
		assertEquals(2, doc.$("p").size());
		assertEquals("one", doc.$("div").first().text());
		assertEquals("two", doc.$("p").first().text());

		assertEquals("four", doc.$("p").last().text());
		assertEquals("three", doc.$("div").last().text());
	}

	@Test
	public void testIterator1() {
		Jerry doc = Jerry.jerry().parse("<div id='one' class='foo'>one</div><div id='two' class='foo'>two</div>");

		Iterator<Jerry> iterator = doc.find(".foo").iterator();
		String result = "";

		while (iterator.hasNext()) {
			Jerry j = iterator.next();
			result += j.attr("id");
		}

		assertEquals("onetwo", result);
	}

	@Test
	public void testIterator2() {
		Jerry doc = Jerry.jerry().parse("<div id='one' class='foo'>one</div><div id='two' class='foo'>two</div>");

		Iterator<Jerry> iterator = doc.find(".notfound").iterator();
		String result = "";

		while (iterator.hasNext()) {
			Jerry j = iterator.next();
			result += j.attr("id");
		}

		assertEquals("", result);
	}

	@Test
	public void testHtmlNodesOwner() {
		Jerry doc = Jerry.jerry().parse("<div>1<div id='x'>2</div>3</div>");

		doc.$("#x").html("<span>wow</span>");

		assertEquals("<div>1<div id=\"x\"><span>wow</span></div>3</div>", doc.html());

		Element divx = doc.get(0).getChildElement(0).getChildElement(0);

		assertSame(doc.get(0), divx.getOwnerDocument());
		assertEquals("span", divx.getChildElement(0).getNodeName());

		assertSame(doc.get(0), divx.getChildElement(0).getOwnerDocument());
		assertSame(doc.get(0), divx.getChildElement(0).getChild(0).getOwnerDocument());
	}
}
