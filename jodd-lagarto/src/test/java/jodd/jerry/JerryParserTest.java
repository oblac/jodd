// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom.jerry;

import junit.framework.TestCase;

public class JerryParserTest extends TestCase {

	public void testJerryParserCreation() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		jerryParser.enableXmlMode();

		Jerry doc = jerryParser.parse("<xml>   <book isbn='123'> <name>Foo<br></name>   </book></xml>");

		Jerry name = doc.$("book name");

		assertEquals("Foo", name.text());

		assertEquals("<xml><book isbn=\"123\"><name>Foo<br></br></name></book></xml>", doc.html());
	}

	public void testAppendContent() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		jerryParser.enableHtmlMode();

		Jerry doc = jerryParser.parse("<xml><book isbn='123'><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book isbn=\"123\"><name>Foo</name><br></book></xml>", doc.html());
	}

	public void testAppendContent2() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		jerryParser.enableXmlMode();

		Jerry doc = jerryParser.parse("<xml><book isbn='123'><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book isbn=\"123\"><name>Foo</name><br></br></book></xml>", doc.html());
	}

	public void testAppendContent3() {
		Jerry.JerryParser jerryParser = Jerry.jerry();

		jerryParser.enableXhtmlMode();

		Jerry doc = jerryParser.parse("<xml><book isbn='123'><name>Foo</name></book></xml>");

		Jerry book = doc.$("book");

		book.append("<br>");

		assertEquals("<xml><book isbn=\"123\"><name>Foo</name><br/></book></xml>", doc.html());
	}
}
