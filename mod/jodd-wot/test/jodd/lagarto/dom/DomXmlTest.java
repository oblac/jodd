// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import jodd.io.FileUtil;
import jodd.util.StringUtil;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
	}

	public void testUpheaWebXml() throws IOException {
		File file = new File(testDataRoot, "uphea-web.xml");
		String xmlContent = FileUtil.readString(file);

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableXmlMode();
		Document doc = lagartoDOMBuilder.parse(xmlContent);

		xmlContent = StringUtil.removeChars(xmlContent, "\n\r\t");
		assertEquals(xmlContent, doc.getHtml());
	}
	
	public void testWhitespaces() throws IOException {
		String xmlContent = "<foo>   <!--c-->  <bar>   </bar>   </foo>";

		LagartoDOMBuilder lagartoDOMBuilder = new LagartoDOMBuilder();
		lagartoDOMBuilder.enableXmlMode();

		Document doc = lagartoDOMBuilder.parse(xmlContent);

		assertEquals(1, doc.getChildNodesCount());

		Element foo = (Element) doc.getChild(0);
		assertEquals("foo", foo.getNodeName());

		assertEquals(2, foo.getChildNodesCount());
		Element bar = (Element) foo.getChild(1);
		assertEquals("bar", bar.getNodeName());

		assertEquals(1, bar.getChildNodesCount());	// must be 1 as whitespaces are between open/closed tag

		assertEquals("<foo><!--c--><bar>   </bar></foo>", doc.getHtml());
	}
}