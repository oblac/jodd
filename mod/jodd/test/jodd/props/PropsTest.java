// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.io.FastCharArrayWriter;
import jodd.io.StreamUtil;
import jodd.util.ClassLoaderUtil;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

public class PropsTest extends TestCase {

	public void testBasic() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test.props"));

		assertEquals(15, p.countTotalProperties());

		assertEquals("Snow White and the Seven Dwarfs", p.getValue("story"));
		assertEquals("Walt Disney's New characters in his first full-length production!", p.getValue("Tagline"));
		assertEquals("C:\\local\\snowwhite.mpg", p.getValue("file"));
		assertEquals("Snow White, pursued by a jealous queen, hides with the Dwarfs; the queen feeds her a poison apple, but Prince Charming awakens her with a kiss.", p.getValue("plot"));

		assertEquals("45.7", p.getValue("bashful.weight"));
		assertEquals("49.5", p.getValue("doc.weight"));

		assertEquals("Čađavi Žar utf8", p.getValue("comment"));

		assertNull(p.getValue("non existing"));


		Properties prop = p.extractProperties();
		assertEquals("1937", prop.getProperty("year"));
		assertEquals("49.5", prop.getProperty("doc.weight"));
		assertEquals("Čađavi Žar utf8", prop.getProperty("comment"));
	}

	public void testEscapeNewValue() throws IOException {
		Props p = new Props();
		p.setEscapeNewLineValue("<br>");
		p.load(readDataFile("test.props"));
		assertEquals("Snow White, pursued by a jealous queen, hides with the Dwarfs; <br>the queen feeds her a poison apple, but Prince Charming <br>awakens her with a kiss.", p.getValue("plot"));
	}

	public void testIgnorePrefixWhitespace() throws IOException {
		Props p = new Props();
		p.setIgnorePrefixWhitespacesOnNewLine(false);
		p.load(readDataFile("test.props"));
		assertEquals("Snow White, pursued by a jealous queen, hides with the Dwarfs; \t\tthe queen feeds her a poison apple, but Prince Charming \t\tawakens her with a kiss.", p.getValue("plot"));
	}

	public void testProfiles() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test-profiles.props"));

		assertEquals("one", p.getValue("foo"));
		assertEquals("one", p.getValue("foo", "non_existing_profile"));
		assertEquals("ten", p.getValue("bar"));

		assertEquals("12345", p.getValue("vitamine", "aaa"));

		assertEquals(5, p.countTotalProperties());

		assertNull(p.getValue("db.url"));
		assertEquals("localhost", p.getValue("db.url", "develop"));
		assertEquals("localhost", p.getValue("db.url", "develop", "deploy"));
		assertEquals("192.168.1.102", p.getValue("db.url", "deploy", "develop"));
		assertEquals("192.168.1.102", p.getValue("db.url", "deploy"));



		Properties prop = p.extractProperties();
		assertEquals("one", prop.getProperty("foo"));

		prop = p.extractProperties("non_existing");
		assertEquals("one", prop.getProperty("foo"));

		prop = p.extractProperties("aaa");
		assertEquals("12345", prop.getProperty("vitamine"));

		prop = p.extractProperties("develop");
		assertEquals("localhost", prop.getProperty("db.url"));
		assertEquals("one", prop.getProperty("foo"));
		prop = p.extractProperties("develop", "deploy");
		assertEquals("localhost", prop.getProperty("db.url"));
		assertEquals("one", prop.getProperty("foo"));
		prop = p.extractProperties("deploy", "develop");
		assertEquals("192.168.1.102", prop.getProperty("db.url"));
		assertEquals("one", prop.getProperty("foo"));
		prop = p.extractProperties("deploy");
		assertEquals("192.168.1.102", prop.getProperty("db.url"));
		assertEquals("one", prop.getProperty("foo"));
	}

	public void testMacros() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test2.props"));

		assertEquals("/app/data", p.getValue("data.path"));
		assertEquals("/app/data2", p.getValue("data.path", "@prof1"));
		assertEquals("/foo/data3", p.getValue("data.path", "@prof2"));

		assertEquals("/roo/re", p.getValue("data.path", "@p1"));
		assertEquals("/app/re", p.getValue("data.path", "@p2"));


		Properties prop = p.extractProperties("@prof2");
		assertEquals("/foo/data3", prop.getProperty("data.path"));
	}

	public void testClone() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test2.props"));

		Props p2 = p.clone();
		p2.load(readDataFile("test.props"));

		assertEquals(2, p.countTotalProperties());
		assertEquals(17, p2.countTotalProperties());

		assertEquals("/app/data", p.getValue("data.path"));
		assertEquals("/app/data2", p.getValue("data.path", "@prof1"));
		assertEquals("/foo/data3", p.getValue("data.path", "@prof2"));
	}

	public void testEmpty() throws IOException {
		Props p = new Props();
		p.setSkipEmptyProps(false);
		p.load(readDataFile("test-e.props"));

		assertEquals(2, p.countTotalProperties());
		assertEquals("good", p.getValue("ok"));
		assertEquals("", p.getValue("empty"));
	}


	private String readDataFile(String fileName) throws IOException {
		String dataFolder = this.getClass().getPackage().getName() + ".data.";
		dataFolder = dataFolder.replace('.', '/');

		InputStream is = ClassLoaderUtil.getResourceAsStream(dataFolder + fileName);
		Writer out = new FastCharArrayWriter();
		StreamUtil.copy(is, out);
		StreamUtil.close(is);
		return out.toString();
	}


}
