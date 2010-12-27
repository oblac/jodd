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

		assertEquals(16, p.countTotalProperties());

		assertEquals("Snow White and the Seven Dwarfs", p.getValue("story"));
		assertEquals("Walt Disney's New characters in his first full-length production!", p.getValue("Tagline"));
		assertEquals("C:\\local\\snowwhite.mpg", p.getValue("file"));
		assertEquals("Snow White, pursued by a jealous queen, hides with the Dwarfs; the queen feeds her a poison apple, but Prince Charming awakens her with a kiss.", p.getValue("plot"));

		assertEquals("45.7", p.getValue("bashful.weight"));
		assertEquals("49.5", p.getValue("doc.weight"));

		assertEquals("Čađavi Žar utf8", p.getValue("comment"));

		assertEquals("foo\tboo\rzoo\nxxx\ftoo", p.getValue("special-chars"));

		assertNull(p.getValue("non existing"));


		Properties prop = p.extractBaseProperties();
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
		assertEquals("one", p.getValue("foo", "qwe"));
		assertEquals("ten", p.getValue("bar"));

		assertEquals("12345", p.getValue("vitamine", "aaa"));

		assertEquals(8, p.countTotalProperties());

		assertNull(p.getValue("db.url"));
		assertEquals("localhost", p.getValue("db.url", "develop"));
		assertEquals("localhost", p.getValue("db.url", "develop", "deploy"));
		assertEquals("192.168.1.102", p.getValue("db.url", "deploy", "develop"));
		assertEquals("192.168.1.102", p.getValue("db.url", "deploy"));

		Properties prop = p.extractBaseProperties();
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

	public void testNestedProfiles() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test-profiles.props"));

		assertEquals("hello", p.getBaseValue("key1"));
		assertEquals("hello", p.getValue("key1"));
		assertEquals("Hi!", p.getValue("key1", "one"));
		assertEquals("Hola!", p.getValue("key1", "one.two"));
		assertEquals("world", p.getValue("key2", "one.two"));
		assertNull(p.getValue("key2", "one"));
		assertEquals("Grazias", p.getValue("key3", "one.two"));
		assertEquals("Grazias", p.getValue("key3", "one"));

		Properties prop = p.extractProperties();
		assertEquals(3, prop.size());
		assertEquals("hello", prop.getProperty("key1"));

		prop = p.extractProperties("one");
		assertEquals(3 + 1, prop.size());
		assertEquals("Hi!", prop.getProperty("key1"));
		assertEquals("Grazias", prop.getProperty("key3"));

		prop = p.extractProperties("one.two");
		assertEquals(3 + 2, prop.size());
		assertEquals("Hola!", prop.getProperty("key1"));
		assertEquals("world", prop.getProperty("key2"));
		assertEquals("Grazias", prop.getProperty("key3"));
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

	public void testMacros2() throws IOException {
		Props p = new Props();
		p.setValue("key1", "**${key${key3}}**");
		p.setValue("key3", "2");
		p.setValue("key2", "++++");

		assertEquals("**++++**", p.getValue("key1"));
	}

	public void testClone() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test2.props"));

		Props p2 = p.clone();
		p2.load(readDataFile("test.props"));

		assertEquals(2, p.countTotalProperties());
		assertEquals(18, p2.countTotalProperties());

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

	public void testActiveProfiles() throws IOException {
		Props p = loadProps("test-actp.props");

		assertEquals("hello", p.getBaseValue("key1"));
		assertEquals("Hola!", p.getValue("key1"));
		assertEquals("world", p.getValue("key2"));
	}

	public void testProperties() throws IOException {
		Props p = loadProps("test.properties");

		assertEquals("value", p.getValue("one"));
		assertEquals("long valuein two lines", p.getValue("two"));
		assertEquals("some utf8 šđžčć", p.getValue("three"));
	}

	public void testAdd() {
		Props p = new Props();
		p.setValue("key1", "val${key2}");

		assertEquals("val", p.getValue("key1"));

		p.setValue("key2", "hurrey\tme!");

		assertEquals("valhurrey\tme!", p.getValue("key1"));
	}

	private String readDataFile(String fileName) throws IOException {
		String dataFolder = this.getClass().getPackage().getName() + ".data.";
		dataFolder = dataFolder.replace('.', '/');

		InputStream is = ClassLoaderUtil.getResourceAsStream(dataFolder + fileName);
		Writer out = new FastCharArrayWriter();
		String encoding = "UTF-8";
		if (fileName.endsWith(".properties")) {
			encoding = "ISO-8859-1";
		}
		StreamUtil.copy(is, out, encoding);
		StreamUtil.close(is);
		return out.toString();
	}

	private Props loadProps(String fileName) throws IOException {
		Props p = new Props();
		String dataFolder = this.getClass().getPackage().getName() + ".data.";
		dataFolder = dataFolder.replace('.', '/');

		InputStream is = ClassLoaderUtil.getResourceAsStream(dataFolder + fileName);
		String encoding = "UTF-8";
		if (fileName.endsWith(".properties")) {
			encoding = "ISO-8859-1";
		}
		p.load(is, encoding);
		return p;
	}


}
