// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom.jerry;

import junit.framework.TestCase;

public class JerryMiscTest extends TestCase {
	
	public void testTextContentDecoding() {
		String html = "<html><body><div>&#1054;&#1076;&#1112;&#1072;&#1074;&#1080; &#1089;&#1077;</div></body></html>";
		
		Jerry doc = Jerry.jerry(html);
		Jerry div = doc.$("div");

		assertEquals(9, div.text().length());
		assertEquals("Одјави се", div.text());
		assertEquals(57, div.html().length());
	}

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
}
