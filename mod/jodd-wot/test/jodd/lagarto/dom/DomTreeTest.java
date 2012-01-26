// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DomTreeTest extends TestCase {

	protected String testDataRoot;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		if (testDataRoot != null) {
			return;
		}
		URL data = NodeSelectorTest.class.getResource("test");
		testDataRoot = data.getFile();
	}

	public void testSpecialCases() {
		Document document = new Document();
		Element html = new Element("html");
		document.appendChild(html);

		assertEquals(0, html.getChildNodesCount());
		assertNull(html.getChild(0));
		assertNull(html.getChild(1000));
		assertEquals(0, html.getChildNodes().length);

		assertNull(html.getFirstChild());
		assertNull(html.getFirstChildElement());
		assertNull(html.getFirstChildElement("h1"));

		assertNull(html.getLastChild());
		assertNull(html.getLastChildElement());
		assertNull(html.getLastChildElement("h1"));

		assertEquals(0, html.getSiblingIndex());
		assertEquals(0, html.getSiblingElementIndex());
		assertEquals(0, html.getSiblingNameIndex());
	}

	public void testDetach() {
		Document document = new Document();
		Element html = new Element("html");
		document.appendChild(html);
		Element div1 = new Element("div");
		html.appendChild(div1);
		Element div2 = new Element("div");
		div1.appendChild(div2);

		div1.detachFromParent();

		assertEquals(0, html.getChildNodesCount());
		assertNull(div1.getParentNode());
	}

	public void testInsertRemoveDeepLevel() {
		Document document = new Document();
		Element html = new Element("html");
		document.appendChild(html);

		Element div1 = new Element("div");
		html.appendChild(div1);
		Element div2 = new Element("div");
		html.appendChild(div2);
		Element div3 = new Element("div");
		html.appendChild(div3);

		assertEquals(2, div1.getDeepLevel());
		assertEquals(2, div2.getDeepLevel());
		assertEquals(2, div3.getDeepLevel());

		assertEquals(3, html.getChildNodesCount());
		assertEquals(3, html.getChildElementsCount());
		assertEquals(3, html.getChildElementsCount("div"));

		assertEquals(div2, html.removeChild(1));
		assertEquals(0, div2.getDeepLevel());

		assertEquals(2, html.getChildNodesCount());
		assertEquals(2, html.getChildElementsCount());
		assertEquals(2, html.getChildElementsCount("div"));

		html.insertAfter(div2, div1);
		assertEquals(2, div2.getDeepLevel());
		assertEquals(3, html.getChildNodesCount());
		assertEquals(3, html.getChildElementsCount());
		assertEquals(3, html.getChildElementsCount("div"));

		Node node[] = html.getChildNodes();
		assertEquals(div1, node[0]);
		assertEquals(div2, node[1]);
		assertEquals(div3, node[2]);

		html.removeChild(1);
		assertEquals(2, html.getChildNodesCount());
		assertEquals(2, html.getChildElementsCount());
		assertEquals(2, html.getChildElementsCount("div"));

		html.insertBefore(div2, div3);
		assertEquals(2, div2.getDeepLevel());
		assertEquals(3, html.getChildNodesCount());
		assertEquals(3, html.getChildElementsCount());
		assertEquals(3, html.getChildElementsCount("div"));

		node = html.getChildNodes();
		assertEquals(div1, node[0]);
		assertEquals(div2, node[1]);
		assertEquals(div3, node[2]);

		html.removeChild(div2);
		node = html.getChildNodes();
		assertEquals(div1, node[0]);
		assertEquals(div3, node[1]);

		html.insertAfter(div2, div3);
		html.insertBefore(div2, div1);

		node = html.getChildNodes();
		assertEquals(div2, node[0]);
		assertEquals(div1, node[1]);
		assertEquals(div3, node[2]);

		assertFalse(div1.hasChildNodes());
		assertTrue(html.hasChildNodes());
	}

	public void testAttributes() {
		Element node = new Element("div");

		assertFalse(node.hasAttributes());
		assertFalse(node.hasAttribute("id"));
		assertNull(node.getAttribute("id"));

		node.setAttribute("id", "jodd");

		assertTrue(node.hasAttributes());
		assertTrue(node.hasAttribute("id"));
		assertEquals("jodd", node.getAttribute("id"));

		node.setAttribute("foo");
		assertTrue(node.hasAttribute("foo"));
		assertNull(node.getAttribute("foo"));

		assertFalse(node.isAttributeIncluding("class", "one"));
		node.setAttribute("class", "  one two  three  ");
		assertTrue(node.isAttributeIncluding("class", "two"));
		assertTrue(node.isAttributeIncluding("class", "three"));

		assertEquals(3, node.getAttributesCount());
	}

	public void testChildren() {
		Element node = new Element("div");

		Text textHello = new Text("hello");
		node.appendChild(textHello);

		Element em = new Element("em");
		node.appendChild(em);
		Text textJodd = new Text("jodd");
		em.appendChild(textJodd);

		Text textHey = new Text("!");
		node.appendChild(textHey);

		assertEquals(3, node.getChildNodesCount());
		assertEquals(1, node.getChildElementsCount());
		assertEquals(1, node.getChildElementsCount("em"));

		Element b = new Element("b");
		node.appendChild(b);
		Text textJodd2 = new Text("fwk");
		b.appendChild(textJodd2);

		assertEquals(4, node.getChildNodesCount());
		assertEquals(2, node.getChildElementsCount());
		assertEquals(1, node.getChildElementsCount("em"));
		assertEquals(1, node.getChildElementsCount("b"));

		assertEquals(b, em.getNextSiblingElement());
		assertNull(em.getNextSiblingName());
		assertNull(em.getPreviousSiblingElement());
		assertNull(em.getPreviousSiblingName());

		assertEquals(em, b.getPreviousSiblingElement());
		assertNull(b.getNextSiblingName());
		assertNull(b.getNextSiblingElement());
		assertNull(b.getPreviousSiblingName());
	}

	public void testCssPath() {
		Document document = new Document();
		Element html = new Element("html");
		document.appendChild(html);
		Element div1 = new Element("div");
		div1.setAttribute("id", "one");
		html.appendChild(div1);
		Element div2 = new Element("div");
		div1.appendChild(div2);

		assertEquals("html div#one div", div2.getCssPath());
		assertEquals("html div#one", div1.getCssPath());
	}

	public void testToWrite() throws IOException {
		Document document = new LagartoDOMBuilder().parse("<html><body><form><input><input><img></form></body></html>");
		String innerHtml = document.getHtml();
		assertEquals("<html><body><form><input><input><img></form></body></html>", innerHtml);

		File file = new File(testDataRoot, "jodd.html");
		String html = FileUtil.readString(file);
		html = StringUtil.replace(html, " />", "/>");
		html = StringUtil.replace(html, "'", "");
		document = new LagartoDOMBuilder().enableXhtmlMode().parse(html);
		innerHtml = document.getHtml();
		assertEquals(html, innerHtml);

		file = new File(testDataRoot, "Twitter.html");
		html = FileUtil.readString(file);
		html = StringUtil.replace(html, " />", "/>");
		html = StringUtil.replace(html, "\" >", "\">");
		html = StringUtil.replace(html, "'", "");
		LagartoDOMBuilder builder = new LagartoDOMBuilder();
		builder.setSelfCloseVoidTags(true);						// use self-closing tags!
		document = builder.parse(html);
		innerHtml = document.getHtml();
		assertEquals(html, innerHtml);

		file = new File(testDataRoot, "Yahoo!.html");
		html = FileUtil.readString(file);
		html = StringUtil.replace(html, " />", "/>");
		html = StringUtil.replace(html, "\" >", "\">");
		html = StringUtil.replace(html, "'", "");
		document = new LagartoDOMBuilder().parse(html);

		NodeSelector nodeSelector = new NodeSelector(document);
		Element div = (Element) nodeSelector.selectFirst("div.ysites-col");
		Element h2 = (Element) div.getFirstChild();

		assertEquals(1, h2.getAttributesCount());
		assertEquals("y-ftr-txt-hdr  ", h2.getAttribute("class"));
		assertTrue(h2.isAttributeIncluding("class", "y-ftr-txt-hdr"));
	}

	public void testBr() throws IOException {
		Document document = new LagartoDOMBuilder().parse("<div><br>some content <br>Some more</div>");
		String innerHtml = document.getHtml();
		assertEquals("<div><br>some content <br>Some more</div>", innerHtml);

		document = new LagartoDOMBuilder().parse("<br>some content <br>Some more");
		innerHtml = document.getHtml();
		assertEquals("<br>some content <br>Some more", innerHtml);
	}
}
