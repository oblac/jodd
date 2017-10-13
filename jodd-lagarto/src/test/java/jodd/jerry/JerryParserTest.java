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

package jodd.jerry;

import jodd.lagarto.dom.Document;
import jodd.lagarto.dom.Element;
import jodd.lagarto.dom.LagartoDOMBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class JerryParserTest {

	@Test
	public void testJerryParserCreation() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableXmlMode();

		Jerry doc = jerryParser.parse("<xml>   <book isbn='123'> <name>Foo<br></name>   </book></xml>");

		Jerry name = doc.$("book name");

		assertEquals("Foo", name.text());

		assertEquals("<xml><book isbn=\"123\"><name>Foo<br></br></name></book></xml>", doc.html());
	}

	@Test
	public void testAppendContent() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableHtmlMode();

		Jerry doc = jerryParser.parse("<xml><book isbn='123'><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book isbn=\"123\"><name>Foo</name><br></book></xml>", doc.html());
	}

	@Test
	public void testAppendContent2() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableXmlMode();

		Jerry doc = jerryParser.parse("<xml><book isbn='123'><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book isbn=\"123\"><name>Foo</name><br></br></book></xml>", doc.html());
	}

	@Test
	public void testAppendContent3() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableXhtmlMode();

		Jerry doc = jerryParser.parse("<xml><book isbn='123'><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book isbn=\"123\"><name>Foo</name><br/></book></xml>", doc.html());
	}

	@Test
	public void testAttributeCaseSensitive() {
		String str = "<dIV id='one' myAttr='aaa'>xxx</dIV>";

		Jerry.JerryParser jerryParser = Jerry.jerry();
		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).enableHtmlMode();

		// default, case not sensitive

		Jerry doc = jerryParser.parse(str);
		Document document = (Document) doc.get(0);
		Element divNode = (Element) document.getChild(0);
		assertEquals("div", divNode.getNodeName());
		assertNotNull(divNode.getAttribute("myattr"));
		assertNotNull(divNode.getAttribute("myAttr"));

		Element divNode2 = (Element) doc.$("div[myattr=aaa]").nodes[0];
		assertSame(divNode, divNode2);

		assertEquals("<div id=\"one\" myattr=\"aaa\">xxx</div>", doc.html());

		// case sensitive

		((LagartoDOMBuilder) jerryParser.getDOMBuilder()).getConfig().setCaseSensitive(true);

		doc = jerryParser.parse(str);
		document = (Document) doc.get(0);
		divNode = (Element) document.getChild(0);
		assertEquals("dIV", divNode.getNodeName());
		assertNull(divNode.getAttribute("myattr"));

		assertEquals("<dIV id=\"one\" myAttr=\"aaa\">xxx</dIV>", doc.html());

		assertEquals(0, doc.$("div[myattr=aaa]").nodes.length);
		divNode2 = (Element) doc.$("dIV[myAttr=aaa]").nodes[0];
		assertSame(divNode, divNode2);
	}
}
