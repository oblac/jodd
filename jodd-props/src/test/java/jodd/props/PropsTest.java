// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.io.FastCharArrayWriter;
import jodd.io.StreamUtil;
import jodd.util.ClassLoaderUtil;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

import static org.junit.Assert.*;

public class PropsTest {

	@Test
	public void testBasic() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test.props"));

		assertEquals(17, p.countTotalProperties());

		assertEquals("Snow White and the Seven Dwarfs", p.getValue("story"));
		assertEquals("Walt Disney's New characters in his first full-length production!", p.getValue("Tagline"));
		assertEquals("C:\\local\\snowwhite.mpg", p.getValue("file"));
		assertEquals("Snow White, pursued by a jealous queen, hides with the Dwarfs; the queen feeds her a poison apple, but Prince Charming awakens her with a kiss.", p.getValue("plot"));

		assertEquals("45.7", p.getValue("bashful.weight"));
		assertEquals("49.5", p.getValue("doc.weight"));

		assertEquals("Čađavi Žar utf8", p.getValue("comment"));

		assertEquals("foo\tboo\rzoo\nxxx\ftoo", p.getValue("special-chars"));
		assertEquals("\\\\a", p.getValue("special2"));
		assertEquals(3, p.getValue("special2").length());

		assertNull(p.getValue("non existing"));

		Properties prop = new Properties();
		p.extractBaseProps(prop);
		assertEquals("1937{c}", prop.getProperty("year"));
		assertEquals("49.5", prop.getProperty("doc.weight"));
		assertEquals("Čađavi Žar utf8", prop.getProperty("comment"));
	}

	@Test
	public void testEscapeNewValue() throws IOException {
		Props p = new Props();
		p.setEscapeNewLineValue("<br>");
		p.load(readDataFile("test.props"));
		assertEquals("Snow White, pursued by a jealous queen, hides with the Dwarfs; <br>the queen feeds her a poison apple, but Prince Charming <br>awakens her with a kiss.", p.getValue("plot"));
	}

	@Test
	public void testIgnorePrefixWhitespace() throws IOException {
		Props p = new Props();
		p.setIgnorePrefixWhitespacesOnNewLine(false);
		p.load(readDataFile("test.props"));
		assertEquals("Snow White, pursued by a jealous queen, hides with the Dwarfs; \t\tthe queen feeds her a poison apple, but Prince Charming \t\tawakens her with a kiss.", p.getValue("plot"));
	}

	@Test
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

		Properties prop = new Properties();
		p.extractBaseProps(prop);
		assertEquals("one", prop.getProperty("foo"));

		prop.clear();
		p.extractProps(prop, "non_existing");
		assertEquals("one", prop.getProperty("foo"));

		prop.clear();
		p.extractProps(prop, "aaa");
		assertEquals("12345", prop.getProperty("vitamine"));

		prop.clear();
		p.extractProps(prop, "develop");
		assertEquals("localhost", prop.getProperty("db.url"));
		assertEquals("one", prop.getProperty("foo"));

		prop.clear();
		p.extractProps(prop, "develop", "deploy");
		assertEquals("localhost", prop.getProperty("db.url"));
		assertEquals("one", prop.getProperty("foo"));

		prop.clear();
		p.extractProps(prop, "deploy", "develop");
		assertEquals("192.168.1.102", prop.getProperty("db.url"));
		assertEquals("one", prop.getProperty("foo"));

		prop.clear();
		p.extractProps(prop, "deploy");
		assertEquals("192.168.1.102", prop.getProperty("db.url"));
		assertEquals("one", prop.getProperty("foo"));

		prop.clear();
		p.setActiveProfiles("deploy");
		p.extractSubProps(prop, "db.*");
		assertEquals(2, prop.size());
	}

	@Test
	public void testDefaultProfile() {
		Props p = new Props();
		p.load(
				"key1=hello\n" +
				"key1<one>=Hi!\n" +
				" \n" +
				"@profiles=one");

		assertEquals("Hi!", p.getValue("key1"));
		assertEquals("Hi!", p.getValue("key1", "one"));
	}

	@Test
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

		Properties prop = new Properties();
		p.extractProps(prop);
		assertEquals(3, prop.size());
		assertEquals("hello", prop.getProperty("key1"));

		prop.clear();
		p.extractProps(prop, "one");
		assertEquals(3 + 1, prop.size());
		assertEquals("Hi!", prop.getProperty("key1"));
		assertEquals("Grazias", prop.getProperty("key3"));

		prop.clear();
		p.extractProps(prop, "one.two");
		assertEquals(3 + 2, prop.size());
		assertEquals("Hola!", prop.getProperty("key1"));
		assertEquals("world", prop.getProperty("key2"));
		assertEquals("Grazias", prop.getProperty("key3"));
	}

	@Test
	public void testMacros() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test2.props"));

		assertEquals("/app/data", p.getValue("data.path"));
		assertEquals("/app/data2", p.getValue("data.path", "@prof1"));
		assertEquals("/foo/data3", p.getValue("data.path", "@prof2"));

		assertEquals("/roo/re", p.getValue("data.path", "@p1"));
		assertEquals("/app/re", p.getValue("data.path", "@p2"));

		Properties prop = new Properties();
		p.extractProps(prop, "@prof2");
		assertEquals("/foo/data3", prop.getProperty("data.path"));
	}

	@Test
	public void testMacros2() throws IOException {
		Props p = new Props();
		p.setValue("key1", "**${key${key3}}**");
		p.setValue("key3", "2");
		p.setValue("key2", "++++");

		assertEquals("**++++**", p.getValue("key1"));
	}

	@Test
	public void testMacroNotExist() {
		Props p = new Props();
		p.setValue("mac1", "value1");
		p.setValue("key1", "${mac1}");
		p.setValue("key2", "${mac2}");

		assertEquals("value1", p.getValue("mac1"));
		assertEquals("value1", p.getValue("key1"));
		assertEquals("${mac2}", p.getValue("key2"));
	}

	@Test
	public void testMacroNotExistIgnoreMissing() {
		Props p = new Props();
		p.setIgnoreMissingMacros(true);
		p.setValue("mac1", "value1");
		p.setValue("key1", "${mac1}");
		p.setValue("key2", "${mac2}");

		assertEquals("value1", p.getValue("mac1"));
		assertEquals("value1", p.getValue("key1"));
		assertNull(p.getValue("key2"));
	}

	@Test
	public void testMacroNotExistSkipEmpty() {
		Props p = new Props();
		p.setIgnoreMissingMacros(true);
		p.setSkipEmptyProps(false);
		p.setValue("mac1", "value1");
		p.setValue("key1", "${mac1}");
		p.setValue("key2", "${mac2}");

		assertEquals("value1", p.getValue("mac1"));
		assertEquals("value1", p.getValue("key1"));
		assertEquals("", p.getValue("key2"));
	}

	@Test
	public void testClone() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test2.props"));

		Props p2 = p.clone();
		p2.load(readDataFile("test.props"));

		assertEquals(2, p.countTotalProperties());
		assertEquals(19, p2.countTotalProperties());

		assertEquals("/app/data", p.getValue("data.path"));
		assertEquals("/app/data2", p.getValue("data.path", "@prof1"));
		assertEquals("/foo/data3", p.getValue("data.path", "@prof2"));
	}

	@Test
	public void testEmpty() throws IOException {
		Props p = new Props();
		p.setSkipEmptyProps(false);
		p.load(readDataFile("test-e.props"));

		assertEquals(2, p.countTotalProperties());
		assertEquals("good", p.getValue("ok"));
		assertEquals("", p.getValue("empty"));
	}

	@Test
	public void testActiveProfiles() throws IOException {
		Props p = loadProps("test-actp.props");

		assertEquals("hello", p.getBaseValue("key1"));
		assertEquals("Hola!", p.getValue("key1"));
		assertEquals("world", p.getValue("key2"));

		assertEquals(1, p.getActiveProfiles().length);
		assertEquals("one.two", p.getActiveProfiles()[0]);
	}

	@Test
	public void testProperties() throws IOException {
		Props p = loadProps("test.properties");

		assertEquals("value", p.getValue("one"));
		assertEquals("long valuein two lines", p.getValue("two"));
		assertEquals("some utf8 šđžčć", p.getValue("three"));
	}

	@Test
	public void testAdd() {
		Props p = new Props();
		p.setValue("key1", "val${key2}");

		assertEquals("val${key2}", p.getValue("key1"));
		assertNull(p.getValue("key1${key2}"));

		p.setValue("key2", "hurrey\tme!");

		assertEquals("valhurrey\tme!", p.getValue("key1"));
	}

	@Test
	public void testDuplicate() throws IOException {
		Props p = new Props();
		loadProps(p, "test-dupl.props");

		assertEquals("three", p.getValue("foo"));
		assertEquals("everywhere", p.getValue("bar", "prof"));

		p = new Props();
		p.setAppendDuplicateProps(true);
		loadProps(p, "test-dupl.props");

		assertEquals("one,two,three", p.getValue("foo"));
		assertEquals("here,there,everywhere", p.getValue("bar", "prof"));
	}

	@Test
	public void testDoubleLoadsAndResolves() {
		Props props = new Props();
		props.load("pojoBean2.val2=123");
		props.load("pojoBean2.val1=\\\\${pojo}");

		assertEquals("123", props.getValue("pojoBean2.val2"));
		// BeanTemplate resolves \${foo} to ${foo}
		// we must be sure that escaped value is not resolved.
		assertEquals("\\${pojo}", props.getValue("pojoBean2.val1"));

		props.load("pojoBean2.val1=\\\\${pojo} ${pojo}");
		assertEquals(2, props.countTotalProperties());
		assertEquals("\\${pojo} ${pojo}", props.getValue("pojoBean2.val1"));
	}

	@Test
	public void testSystemProperties() {
		Props props = new Props();
		assertEquals(0, props.countTotalProperties());
		assertNull(props.getValue("user.dir"));

		props.loadSystemProperties("sys");
		assertTrue(props.countTotalProperties() > 0);
		assertNotNull(props.getValue("sys.user.dir"));
	}

	@Test
	public void testEnvironment() {
		Props props = new Props();
		assertEquals(0, props.countTotalProperties());

		props.loadEnvironment("env");
		assertTrue(props.countTotalProperties() > 0);
	}

	@Test
	public void testValueWithBracket() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test3.props"));

		assertEquals("info@jodd.org;patrick@jodd.org", p.getValue("email.from"));
		assertEquals("[ERROR] Got %s exceptions", p.getValue("email.subject"));
		assertEquals("line1line2line3", p.getValue("email.text"));

		p = new Props();
		p.setIgnorePrefixWhitespacesOnNewLine(false);
		p.load(readDataFile("test3.props"));

		assertEquals("info@jodd.org;patrick@jodd.org", p.getValue("email.from"));
		assertEquals("[ERROR] Got %s exceptions", p.getValue("email.subject"));
		assertEquals("line1\tline2line3", p.getValue("email.text"));

		p = new Props();
		p.setIgnorePrefixWhitespacesOnNewLine(false);
		p.setEscapeNewLineValue("\n");
		p.load(readDataFile("test3.props"));

		assertEquals("info@jodd.org;patrick@jodd.org", p.getValue("email.from"));
		assertEquals("[ERROR] Got %s exceptions", p.getValue("email.subject"));
		assertEquals("line1\n\tline2\nline3", p.getValue("email.text"));
	}

	@Test
	public void testMultilineValue() throws IOException {
		Props p = new Props();
		p.setValueTrimLeft(true);
		p.load(readDataFile("test3.props"));

		assertEquals("\r\n\tHello from\r\n\tthe multiline\r\n\tvalue\r\n", p.getValue("email.footer"));
		assertEquals("aaa", p.getValue("email.header"));
	}

	@Test
	public void testAppend() {
		Props p = new Props();
		p.setAppendDuplicateProps(true);
		p.load("foo=123\nfoo=456");
		assertEquals("123,456", p.getValue("foo"));

		p = new Props();
		p.load("foo=123\nfoo+=456");
		assertEquals("123,456", p.getValue("foo"));
	}

	@Test
	public void testAppend2() {
		Props p = new Props();
		p.setAppendDuplicateProps(false);
		p.load("foo=one\nfoo=two\nfoo+=three");
		assertEquals("two,three", p.getValue("foo"));

		p = new Props();
		p.setAppendDuplicateProps(true);
		p.load("foo=one\nfoo=two\nfoo+=three");
		assertEquals("one,two,three", p.getValue("foo"));

		p = new Props();
		p.setAppendDuplicateProps(false);
		p.load("foo=one\nfoo=two\nfoo+=three\nfoo=four");
		assertEquals("four", p.getValue("foo"));
	}

	@Test
	public void testAppendEof() {
		Props p = new Props();
		p.setAppendDuplicateProps(false);
		p.load("foo=one\nfoo=two\nfoo+");
		assertEquals("two", p.getValue("foo"));
	}

	@Test
	public void testActiveProfileBeforeInit() {
		Props p =  new Props();
		p.setActiveProfiles("xxx");
		p.load("foo=one");
		assertNotNull(p.getActiveProfiles());
		assertEquals("xxx", p.getActiveProfiles()[0]);
	}

	@Test
	public void testDoubleInitialization() {
		Props p =  new Props();
		p.setValue("bar", "two.${foo}.${wer}");
		p.setValue("foo", "one");

		assertEquals("two.one.${wer}", p.getValue("bar"));

		p.setValue("wer", "zero");

		assertEquals("two.one.zero", p.getValue("bar"));
	}

	@Test
	public void testCategoriesInValues() {
		Props p =  new Props();
		p.load(	"[section]\n" +
				"foo = aaa, [bbb:ccc]\n" +
				"bar = teapot");

		assertEquals("aaa, [bbb:ccc]", p.getValue("section.foo"));
		assertEquals("teapot", p.getValue("section.bar"));
	}

	@Test
	public void testDuplicatedValue() {
		Props p = new Props();
		p.setValue("foo", "bar");
		p.setValue("foo", "aaa", "prof1");
		p.setValue("foo", "bbb", "prof2");

		assertEquals("bar", p.getValue("foo"));
		assertEquals("aaa", p.getValue("foo", "prof1"));
		assertEquals("bbb", p.getValue("foo", "prof2"));

		assertEquals("aaa", p.getValue("foo", "prof1", "prof2"));
		assertEquals("bbb", p.getValue("foo", "prof2", "prof1"));
	}

	// ---------------------------------------------------------------- util

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

	private Props loadProps(Props p, String fileName) throws IOException {
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

	private Props loadProps(String fileName) throws IOException {
		Props p = new Props();
		return loadProps(p, fileName);
	}

}
