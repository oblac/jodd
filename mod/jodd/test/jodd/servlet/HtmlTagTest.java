// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import junit.framework.TestCase;

public class HtmlTagTest extends TestCase {

	public void testTagName() {
		String body = "<html>";
		HtmlTag tag = new HtmlTag(body);
		assertFalse(tag.isEndTag());
		assertFalse(tag.isClosedTag());
		assertEquals("html", tag.getTagName());
		assertEquals(0, tag.totalAttributes());

		body = "<   TaG atr='val'>";
		tag = new HtmlTag(body);
		assertEquals("tag", tag.getTagName());
		assertEquals(1, tag.totalAttributes());
		assertEquals("val", tag.getAttribute("atr"));

		body = "</html>";
		tag = new HtmlTag(body);
		assertTrue(tag.isEndTag());
		assertFalse(tag.isClosedTag());
		assertEquals("html", tag.getTagName());
		assertEquals(0, tag.totalAttributes());

		body = "</        html>";
		tag = new HtmlTag(body);
		assertTrue(tag.isEndTag());
		assertFalse(tag.isClosedTag());
		assertEquals("html", tag.getTagName());
		assertEquals(0, tag.totalAttributes());

		body = "<html/>";
		tag = new HtmlTag(body);
		assertFalse(tag.isEndTag());
		assertTrue(tag.isClosedTag());
		assertEquals("html", tag.getTagName());
		assertEquals(0, tag.totalAttributes());

		body = "<    html       />";
		tag = new HtmlTag(body);
		assertFalse(tag.isEndTag());
		assertTrue(tag.isClosedTag());
		assertEquals("html", tag.getTagName());
		assertEquals(0, tag.totalAttributes());
	}

	public void testAttributes() {
		String body = "<select name=\"Marie\">";
		HtmlTag tag = new HtmlTag(body);
		assertFalse(tag.isEndTag());
		assertFalse(tag.isClosedTag());
		assertEquals("select", tag.getTagName());
		assertTrue(tag.hasAttribute("name"));
		assertEquals("Marie", tag.getAttribute("name"));
		assertEquals(body, tag.toString());
		assertNull(tag.getAttribute("foo"));
		assertEquals(1, tag.totalAttributes());

		body = "<select name=\"Marie\" value='foo'      >";
		tag = new HtmlTag(body);
		assertFalse(tag.isEndTag());
		assertFalse(tag.isClosedTag());
		assertEquals("select", tag.getTagName());
		assertEquals(2, tag.totalAttributes());
		assertTrue(tag.hasAttribute("name"));
		assertEquals("Marie", tag.getAttribute("name"));
		assertEquals(body, tag.toString());
		assertTrue(tag.hasAttribute("value"));
		assertEquals("foo", tag.getAttribute("value"));
		assertEquals(body, tag.toString());
	}


	public void testAddValue() {
		String body = "<input name=\"xxx\">";
		HtmlTag tag = new HtmlTag(body);
		assertEquals(1, tag.totalAttributes());

		tag.setAttribute("value", "qwerty");
		assertEquals("<input name=\"xxx\" value=\"qwerty\">", tag.toString());

		tag.setAttribute("value", "123\"");
		assertEquals("<input name=\"xxx\" value=\"123&quot;\">", tag.toString());

		tag.setAttribute("value");
		assertEquals("<input name=\"xxx\" value>", tag.toString());

		tag.removeAttribute("value");
		assertEquals("<input name=\"xxx\">", tag.toString());

		tag.removeAttribute("name");
		assertEquals("<input>", tag.toString());

	}

	public void testLocate() {
		String html = "<form name=\"form2\" method=\"post\" action=\"postForm.exec.html\">\n";
		HtmlTag tag = HtmlTag.locateNextTag(html, 0);
		assertNotNull(tag);
		assertEquals("form", tag.getTagName());
		assertEquals(3, tag.totalAttributes());
		assertEquals(html.trim(), tag.toString());

		html = "text <!-- test --> <!-- doo --> <foo>";
		tag = HtmlTag.locateNextTag(html, 0);
		assertNotNull(tag);
		assertEquals("foo", tag.getTagName());

		html = "Checkbox <small><i>(has value, String)</i></small>";
		tag = HtmlTag.locateNextTag(html, 0);
		assertNotNull(tag);
		assertEquals("small", tag.getTagName());
		assertEquals("<small>", tag.toString());
		assertEquals(html.indexOf("<i>"), tag.getNextIndex());

		tag = HtmlTag.locateNextTag(html, tag.getNextIndex());
		assertNotNull(tag);
		assertEquals("i", tag.getTagName());
		assertEquals("<i>", tag.toString());
		assertEquals(html.indexOf("(has"), tag.getNextIndex());
	}


}
