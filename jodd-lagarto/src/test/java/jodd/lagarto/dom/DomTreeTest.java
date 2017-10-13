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
import jodd.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class DomTreeTest {

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
	public void testSpecialCases() {
		Document document = new Document();

		Element html = new Element(document, "html");
		document.addChild(html);

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

		assertTrue(document.check());
	}

	@Test
	public void testDetach() {
		Document document = new Document();

		Element html = new Element(document, "html");
		document.addChild(html);
		Element div1 = new Element(document, "div");
		html.addChild(div1);
		Element div2 = new Element(document, "div");
		div1.addChild(div2);

		div1.detachFromParent();

		assertEquals(0, html.getChildNodesCount());
		assertNull(div1.getParentNode());

		assertTrue(document.check());
	}

	@Test
	public void testInsertRemoveDeepLevel() {
		Document document = new Document();

		Element html = new Element(document, "html");
		document.addChild(html);

		Element div1 = new Element(document, "div");
		html.addChild(div1);
		Element div2 = new Element(document, "div");
		html.addChild(div2);
		Element div3 = new Element(document, "div");
		html.addChild(div3);

		assertEquals(3, html.getChildNodesCount());
		assertEquals(3, html.getChildElementsCount());
		assertEquals(3, html.getChildElementsCount("div"));

		assertEquals(div2, html.removeChild(1));

		assertEquals(2, html.getChildNodesCount());
		assertEquals(2, html.getChildElementsCount());
		assertEquals(2, html.getChildElementsCount("div"));

		html.insertAfter(div2, div1);
		assertEquals(3, html.getChildNodesCount());
		assertEquals(3, html.getChildElementsCount());
		assertEquals(3, html.getChildElementsCount("div"));

		Node[] node = html.getChildNodes();
		assertEquals(div1, node[0]);
		assertEquals(div2, node[1]);
		assertEquals(div3, node[2]);

		html.removeChild(1);
		assertEquals(2, html.getChildNodesCount());
		assertEquals(2, html.getChildElementsCount());
		assertEquals(2, html.getChildElementsCount("div"));

		html.insertBefore(div2, div3);
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

		assertTrue(document.check());
	}

	@Test
	public void testAttributes() {
		Document document = new Document();

		Element node = new Element(document, "div");

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

		assertFalse(node.isAttributeContaining("class", "one"));
		node.setAttribute("class", "  one two  three  ");
		assertTrue(node.isAttributeContaining("class", "two"));
		assertTrue(node.isAttributeContaining("class", "three"));

		assertEquals(3, node.getAttributesCount());
	}

	@Test
	public void testChildren() {
		Document document = new Document();

		Element node = new Element(document, "div");

		Text textHello = new Text(document, "hello");
		node.addChild(textHello);

		Element em = new Element(document, "em");
		node.addChild(em);
		Text textJodd = new Text(document, "jodd");
		em.addChild(textJodd);

		Text textHey = new Text(document, "!");
		node.addChild(textHey);

		assertEquals(3, node.getChildNodesCount());
		assertEquals(1, node.getChildElementsCount());
		assertEquals(1, node.getChildElementsCount("em"));

		Element b = new Element(document, "b");
		node.addChild(b);
		Text textJodd2 = new Text(document, "fwk");
		b.addChild(textJodd2);

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

		assertTrue(node.check());
	}

	@Test
	public void testCssPath() {
		Document document = new Document();

		Element html = new Element(document, "html");
		document.addChild(html);
		Element div1 = new Element(document, "div");
		div1.setAttribute("id", "one");
		html.addChild(div1);
		Element div2 = new Element(document, "div");
		div1.addChild(div2);

		assertEquals("html div#one div", div2.getCssPath());
		assertEquals("html div#one", div1.getCssPath());

		assertTrue(document.check());
	}

	@Test
	public void testToWrite() throws IOException {
		Document document = new LagartoDOMBuilder().parse("<html><body><form><input><input><img></form></body></html>");
		String innerHtml = document.getHtml();
		assertEquals("<html><body><form><input><input><img></form></body></html>", innerHtml);
		assertTrue(document.check());

		File file = new File(testDataRoot, "jodd.html");
		String html = FileUtil.readString(file);
		html = StringUtil.replace(html, " />", "/>");
		html = StringUtil.replace(html, "'", "");
		document = new LagartoDOMBuilder().enableXhtmlMode().parse(html);
		innerHtml = document.getHtml();
		innerHtml = StringUtil.replace(innerHtml, "»", "&raquo;");
		innerHtml = StringUtil.replace(innerHtml, " ", "&nbsp;");
		innerHtml = StringUtil.replace(innerHtml, "·", "&middot;");

		assertEquals(html, innerHtml);
		assertTrue(document.check());

		file = new File(testDataRoot, "Twitter.html");
		html = FileUtil.readString(file);
		html = StringUtil.replace(html, " />", "/>");
		html = StringUtil.replace(html, "\" >", "\">");
		html = StringUtil.replace(html, "'", "");
		html = StringUtil.replace(html, "&#32;", " ");
		LagartoDOMBuilder builder = new LagartoDOMBuilder();
		builder.getConfig().setSelfCloseVoidTags(true);                        // use self-closing tags!
		builder.getConfig().setEnableConditionalComments(true).setCondCommentIEVersion(6);
		document = builder.parse(html);
		innerHtml = document.getHtml();
		innerHtml = StringUtil.replace(innerHtml, "»", "&raquo;");
		innerHtml = StringUtil.replace(innerHtml, " ", "&nbsp;");
		innerHtml = StringUtil.replace(innerHtml, "·", "&middot;");
		innerHtml = StringUtil.replace(innerHtml, "’", "&#8217;");
		innerHtml = StringUtil.replace(innerHtml, "©", "&copy;");

		html = StringUtil.replace(html, "<!--[if lte IE 6]>", "");
		html = StringUtil.replace(html, "<![endif]-->", "");

		assertEquals(html, innerHtml);
		assertTrue(document.check());

		file = new File(testDataRoot, "Yahoo!.html");
		html = FileUtil.readString(file);
		html = StringUtil.replace(html, " />", "/>");
		html = StringUtil.replace(html, "\" >", "\">");
		html = StringUtil.replace(html, "'", "");
		document = new LagartoDOMBuilder().parse(html);

		NodeSelector nodeSelector = new NodeSelector(document);
		Element div = (Element) nodeSelector.selectFirst("div.ysites-col");
		Element h2 = (Element) div.getFirstChild();

		assertEquals(2, h2.getAttributesCount());
		assertEquals("y-ftr-txt-hdr  ", h2.getAttribute("class"));
		assertTrue(h2.isAttributeContaining("class", "y-ftr-txt-hdr"));
		assertTrue(h2.hasAttribute("\""));

		assertTrue(document.check());
	}

	@Test
	public void testBr() throws IOException {
		Document document = new LagartoDOMBuilder().parse("<div><br>some content <br>Some more</div>");
		String innerHtml = document.getHtml();
		assertEquals("<div><br>some content <br>Some more</div>", innerHtml);
		assertTrue(document.check());

		document = new LagartoDOMBuilder().parse("<br>some content <br>Some more");
		innerHtml = document.getHtml();
		assertEquals("<br>some content <br>Some more", innerHtml);
		assertTrue(document.check());
	}

	@Test
	public void testReindexOne() {
		Document document = new Document();

		Element one = new Element(document, "one");
		document.addChild(one);

		assertEquals(1, document.childElementNodesCount);
		assertEquals(0, one.siblingElementIndex);

		Element two = new Element(document, "two");
		document.addChild(two);

		assertEquals(2, document.childElementNodesCount);
		assertEquals(0, one.siblingElementIndex);
		assertEquals(1, two.siblingElementIndex);

		Text three = new Text(document, "xxx");
		document.addChild(three);

		Element four = new Element(document, "four");
		document.addChild(four);

		assertEquals(3, document.childElementNodesCount);
		assertEquals(0, one.siblingElementIndex);
		assertEquals(1, two.siblingElementIndex);
		assertEquals(2, four.siblingElementIndex);
	}

	@Test
	public void testHasVsGet333() {
		Document document = new Document();

		Element one = new Element(document, "one");
		document.addChild(one);
		one.setAttribute("a1", "v1");

		assertEquals("v1", one.getAttribute("a1"));
		assertEquals("v1", one.getAttribute("A1"));

		assertTrue(one.hasAttribute("a1"));
		assertTrue(one.hasAttribute("A1"));

	}
}
