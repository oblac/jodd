// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.jerry;

import jodd.csselly.selector.PseudoClass;
import jodd.csselly.selector.PseudoClassSelector;
import jodd.csselly.selector.PseudoFunction;
import jodd.csselly.selector.PseudoFunctionSelector;
import jodd.lagarto.dom.Element;
import jodd.lagarto.dom.LagartoDOMBuilder;
import jodd.lagarto.dom.Node;
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

	@Test
	public void testContains() {
		Jerry doc = Jerry.jerry().parse("<body>aaa<p>foo 401(k) bar</p>xxx</body>");

		Jerry p = doc.$("p:contains('401(k)')");
		assertEquals(1, p.size());

		p = doc.$("p:contains('402(k)')");
		assertEquals(0, p.size());
	}

	@Test
	public void testCustomPseudoClass() {
		PseudoClassSelector.registerPseudoClass(MyPseudoClass.class);

		Jerry doc = Jerry.jerry().parse("<body><p jodd-attr='1'>found</p><p>not found</p></body>");

		Jerry p = doc.$("p:jjjjj");
		assertEquals(1, p.size());
		assertEquals("found", p.text());
	}

	public static class MyPseudoClass extends PseudoClass {
		@Override
		public boolean match(Node node) {
			return node.hasAttribute("jodd-attr");
		}

		@Override
		public String getPseudoClassName() {
			return "jjjjj";
		}
	}

	@Test
	public void testCustomPseudoFunction() {
		PseudoFunctionSelector.registerPseudoFunction(MyPseudoFunction.class);

		Jerry doc = Jerry.jerry().parse("<body><p>not found</p><div>This!</div></body>");

		Jerry p = doc.$(":super-fn(3)");
		assertEquals(1, p.size());
		assertEquals("This!", p.text());
	}

	public static class MyPseudoFunction extends PseudoFunction {
		@Override
		public Object parseExpression(String expression) {
			return Integer.valueOf(expression);
		}

		@Override
		public boolean match(Node node, Object expression) {
			Integer size = (Integer) expression;
			return node.getNodeName().length() == size.intValue();
		}

		@Override
		public String getPseudoFunctionName() {
			return "super-fn";
		}
	}

}
