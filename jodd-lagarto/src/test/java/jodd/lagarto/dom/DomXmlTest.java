// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DomXmlTest extends TestCase {
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

	public void testPeopleXml() throws IOException {
		File file = new File(testDataRoot, "people.xml");
		String xmlContent = FileUtil.readString(file);

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableXmlMode();
		Document doc = lagartoDOMBuilder.parse(xmlContent);

		assertEquals(2, doc.getChildNodesCount());	// not 3!

		XmlDeclaration xml = (XmlDeclaration) doc.getFirstChild();
		assertEquals(3, xml.getAttributesCount());

		Element peopleList = (Element) doc.getChild(1);
		assertEquals(1, peopleList.getChildNodesCount());

		Element person = (Element) peopleList.getFirstChildElement();
		assertEquals(3, person.getChildNodesCount());

		Element name = (Element) person.getChild(0);
		assertEquals("Fred Bloggs", name.getTextContent());
		assertEquals("Male", person.getChild(2).getTextContent());

		xmlContent = StringUtil.removeChars(xmlContent, "\n\r\t");
		assertEquals(xmlContent, doc.getHtml());

		assertTrue(doc.check());
	}

	public void testUpheaWebXml() throws IOException {
		File file = new File(testDataRoot, "uphea-web.xml");
		String xmlContent = FileUtil.readString(file);

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableXmlMode();
		Document doc = lagartoDOMBuilder.parse(xmlContent);

		xmlContent = StringUtil.removeChars(xmlContent, "\n\r\t");
		assertEquals(xmlContent, doc.getHtml());

		assertTrue(doc.check());
	}
	
	public void testWhitespaces() throws IOException {
		String xmlContent = "<foo>   <!--c-->  <bar>   </bar> <x/> </foo>";

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableXmlMode();
		lagartoDOMBuilder.setSelfCloseVoidTags(true);

		Document doc = lagartoDOMBuilder.parse(xmlContent);

		assertEquals(1, doc.getChildNodesCount());

		Element foo = (Element) doc.getChild(0);
		assertEquals("foo", foo.getNodeName());

		assertEquals(3, foo.getChildNodesCount());
		Element bar = (Element) foo.getChild(1);
		assertEquals("bar", bar.getNodeName());

		assertEquals(1, bar.getChildNodesCount());	// must be 1 as whitespaces are between open/closed tag

		assertEquals("<foo><!--c--><bar>   </bar><x/></foo>", doc.getHtml());

		assertTrue(doc.check());
	}

	public void testIgnoreComments() throws IOException {
		String xmlContent = "<foo>   <!--c-->  <bar>   </bar> <!--c--> <x/> <!--c--> </foo>";

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableXmlMode();
		lagartoDOMBuilder.setIgnoreComments(true);

		Document doc = lagartoDOMBuilder.parse(xmlContent);

		assertEquals("<foo><bar>   </bar><x></x></foo>", doc.getHtml());

		assertTrue(doc.check());
	}

	public void testConditionalComments() throws IOException {
		String xmlContent = "<foo><!--[if !IE]>--><bar>Jodd</bar><!--<![endif]--></foo>";

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableXmlMode();
		lagartoDOMBuilder.setIgnoreComments(true);

		Document doc = lagartoDOMBuilder.parse(xmlContent);

		assertEquals("<foo><bar>Jodd</bar></foo>", doc.getHtml());

		assertTrue(doc.check());
	}

	public void testConditionalComments2() throws IOException {
		String xmlContent = "<foo><![if !IE]><bar>Jodd</bar></foo>";

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableXmlMode();
		lagartoDOMBuilder.setIgnoreComments(true);
		lagartoDOMBuilder.setCollectErrors(true);
		lagartoDOMBuilder.setCalculatePosition(true);

		Document doc = lagartoDOMBuilder.parse(xmlContent);
		List<String> errors = lagartoDOMBuilder.getErrors();

		assertEquals(1, errors.size());
		assertTrue(errors.get(0).contains("[1:5 @5]"));
		assertEquals("<foo><bar>Jodd</bar></foo>", doc.getHtml());

		assertTrue(doc.check());
	}
}