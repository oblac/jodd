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

package jodd.props;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PropsTest extends BasePropsTest {

	@Test
	void testBasic() throws IOException {
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
		p.extractProps(prop, null);
		assertEquals("1937{c}", prop.getProperty("year"));
		assertEquals("49.5", prop.getProperty("doc.weight"));
		assertEquals("Čađavi Žar utf8", prop.getProperty("comment"));
	}

	@Test
	void testEscapeNewValue() throws IOException {
		Props p = new Props();
		p.setEscapeNewLineValue("<br>");
		p.load(readDataFile("test.props"));
		assertEquals("Snow White, pursued by a jealous queen, hides with the Dwarfs; <br>the queen feeds her a poison apple, but Prince Charming <br>awakens her with a kiss.", p.getValue("plot"));
	}

	@Test
	void testIgnorePrefixWhitespace() throws IOException {
		Props p = new Props();
		p.setIgnorePrefixWhitespacesOnNewLine(false);
		p.load(readDataFile("test.props"));
		assertEquals("Snow White, pursued by a jealous queen, hides with the Dwarfs; \t\tthe queen feeds her a poison apple, but Prince Charming \t\tawakens her with a kiss.", p.getValue("plot"));
	}

	@Test
	void testProfiles() throws IOException {
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
		p.extractProps(prop, null);
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
	void testDefaultProfile() {
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
	void testNestedProfiles() throws IOException {
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
	void testMacros() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test2.props"));

		assertEquals("/roo/mypath", p.getValue("data.mypath"));

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
	void testMacrosNew() throws IOException {
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

		// activate profiles

		p.setActiveProfiles("@prof2");
		assertEquals("/foo/data3", p.getValue("data.path", "@prof2"));

		p.setActiveProfiles("@p1", "@p2");
		assertEquals("/app/re", p.getValue("data.path", "@p2"));
	}


	@Test
	void testMacros2() throws IOException {
		Props p = new Props();
		p.setValue("key1", "**${key${key3}}**");
		p.setValue("key3", "2");
		p.setValue("key2", "++++");

		assertEquals("**++++**", p.getValue("key1"));
	}

	@Test
	void testMacroNotExist() {
		Props p = new Props();
		p.setValue("mac1", "value1");
		p.setValue("key1", "${mac1}");
		p.setValue("key2", "${mac2}");

		assertEquals("value1", p.getValue("mac1"));
		assertEquals("value1", p.getValue("key1"));
		assertEquals("${mac2}", p.getValue("key2"));
	}

	@Test
	void testMacroNotExistIgnoreMissing() {
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
	void testMacroNotExistSkipEmpty() {
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
	void testClone() throws IOException {
		Props p = new Props();
		p.load(readDataFile("test2.props"));

		Props p2 = p.clone();
		p2.load(readDataFile("test.props"));

		assertEquals(3, p.countTotalProperties());
		assertEquals(20, p2.countTotalProperties());

		assertEquals("/app/data", p.getValue("data.path"));
		assertEquals("/app/data2", p.getValue("data.path", "@prof1"));
		assertEquals("/foo/data3", p.getValue("data.path", "@prof2"));
	}

	@Test
	void testEmpty() throws IOException {
		Props p = new Props();
		p.setSkipEmptyProps(false);
		p.load(readDataFile("test-e.props"));

		assertEquals(2, p.countTotalProperties());
		assertEquals("good", p.getValue("ok"));
		assertEquals("", p.getValue("empty"));
	}

	@Test
	void testActiveProfiles() throws IOException {
		Props p = loadProps("test-actp.props");

		assertEquals("hello", p.getBaseValue("key1"));
		assertEquals("Hola!", p.getValue("key1"));
		assertEquals("world", p.getValue("key2"));

		assertEquals(1, p.getActiveProfiles().length);
		assertEquals("one.two", p.getActiveProfiles()[0]);
	}

	@Test
	void testProperties() throws IOException {
		Props p = loadProps("test.properties");

		assertEquals("value", p.getValue("one"));
		assertEquals("long valuein two lines", p.getValue("two"));
		assertEquals("some utf8 šđžčć", p.getValue("three"));
	}

	@Test
	void testAdd() {
		Props p = new Props();
		p.setValue("key1", "val${key2}");

		assertEquals("val${key2}", p.getValue("key1"));
		assertNull(p.getValue("key1${key2}"));

		p.setValue("key2", "hurrey\tme!");

		assertEquals("valhurrey\tme!", p.getValue("key1"));
	}

	@Test
	void testDuplicate() throws IOException {
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
	void testDoubleLoadsAndResolves() {
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
	void testSystemProperties() {
		Props props = new Props();
		assertEquals(0, props.countTotalProperties());
		assertNull(props.getValue("user.dir"));

		props.loadSystemProperties("sys");
		assertTrue(props.countTotalProperties() > 0);
		assertNotNull(props.getValue("sys.user.dir"));
	}

	@Test
	void testEnvironment() {
		Props props = new Props();
		assertEquals(0, props.countTotalProperties());

		props.loadEnvironment("env");
		assertTrue(props.countTotalProperties() > 0);
	}

	@Test
	void testValueWithBracket() throws IOException {
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
	void testMultilineValue() throws IOException {
		Props p = new Props();
		p.setValueTrimLeft(true);
		p.load(readDataFile("test3.props"));

		assertEquals(System.lineSeparator() + "\tHello from" + System.lineSeparator() + "\tthe multiline" + System.lineSeparator() + "\tvalue" + System.lineSeparator() , p.getValue("email.footer"));
		assertEquals("aaa", p.getValue("email.header"));
	}

	@Test
	void testAppend() {
		Props p = new Props();
		p.setAppendDuplicateProps(true);
		p.load("foo=123\nfoo=456");
		assertEquals("123,456", p.getValue("foo"));

		p = new Props();
		p.load("foo=123\nfoo+=456");
		assertEquals("123,456", p.getValue("foo"));
	}

	@Test
	void testAppend2() {
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
	void testAppendEof() {
		Props p = new Props();
		p.setAppendDuplicateProps(false);
		p.load("foo=one\nfoo=two\nfoo+");
		assertEquals("two", p.getValue("foo"));
	}

	@Test
	void testActiveProfileBeforeInit() {
		Props p =  new Props();
		p.setActiveProfiles("xxx");
		p.load("foo=one");
		assertNotNull(p.getActiveProfiles());
		assertEquals("xxx", p.getActiveProfiles()[0]);
	}

	@Test
	void testDoubleInitialization() {
		Props p =  new Props();
		p.setValue("bar", "two.${foo}.${wer}");
		p.setValue("foo", "one");

		assertEquals("two.one.${wer}", p.getValue("bar"));

		p.setValue("wer", "zero");

		assertEquals("two.one.zero", p.getValue("bar"));
	}

	@Test
	void testCategoriesInValues() {
		Props p =  new Props();
		p.load(	"[section]\n" +
				"foo = aaa, [bbb:ccc]\n" +
				"bar = teapot");

		assertEquals("aaa, [bbb:ccc]", p.getValue("section.foo"));
		assertEquals("teapot", p.getValue("section.bar"));
	}

	@Test
	void testDuplicatedValue() {
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

	@Test
	void testIteratorEmpty() {
		Props p = new Props();

		Iterator<PropsEntry> it = p.iterator();

		assertFalse(it.hasNext());

		try {
			it.next();
			fail("error");
		} catch (Exception ignore) {
		}
	}

	@Test
	void testIteratorSkip() {
		Props p = new Props();

		p.load("zorg<prof2>=zero\n" +
				"foo=one\n" +
				"bar=two\n" +
				"foo<prof1>=zero");

		Iterator<PropsEntry> it = p.iterator();

		assertTrue(it.hasNext());

		PropsEntry pe = it.next();
		assertEquals("foo", pe.getKey());
		pe = it.next();
		assertEquals("bar", pe.getKey());

		assertFalse(it.hasNext());
		try {
			it.next();
			fail("error");
		} catch (Exception ignore) {
		}

		p.setActiveProfiles("prof1", "prof2");

		it = p.iterator();
		assertEquals("zorg", it.next().getKey());
		assertEquals("foo", it.next().getKey());
		assertEquals("bar", it.next().getKey());
		assertEquals("foo", it.next().getKey());
		assertFalse(it.hasNext());

		it = p.entries().activeProfiles().skipDuplicatesByValue().iterator();

		assertEquals("zorg", it.next().getKey());
		assertEquals("bar", it.next().getKey());
		assertEquals("foo", it.next().getKey());
		assertFalse(it.hasNext());

		it = p.entries().profile("prof1").skipDuplicatesByValue().iterator();
		assertEquals("bar", it.next().getKey());
		assertEquals("foo", it.next().getKey());
		assertFalse(it.hasNext());


		it = p.entries().activeProfiles().skipDuplicatesByPosition().iterator();

		assertEquals("zorg", it.next().getKey());
		assertEquals("foo", it.next().getKey());
		assertEquals("bar", it.next().getKey());
		assertFalse(it.hasNext());

		it = p.entries().profile("prof1").skipDuplicatesByPosition().iterator();
		assertEquals("foo", it.next().getKey());
		assertEquals("bar", it.next().getKey());
		assertFalse(it.hasNext());
	}

	@Test
	void testIteratorSections() {
		Props p = new Props();

		p.load("aaa.zorg<prof2>=zero\n" +
				"bbb.foo=one\n" +
				"ccc.bar=two\n" +
				"bbb.foo<prof1>=zero");


		p.setActiveProfiles("prof1", "prof2");

		Iterator<PropsEntry> it = p.entries().section("bbb").profile("prof1", "prof2").iterator();
		assertEquals("bbb.foo", it.next().getKey());
		assertEquals("bbb.foo", it.next().getKey());
		assertFalse(it.hasNext());
	}

	@Test
	void testGetAllProfiles() {
		Props p = new Props();

		p.load("zorg<prof2>=zero\n" +
				"foo=one\n" +
				"bar=two\n" +
				"foo<prof1>=zero");

		String[] profiles = p.getAllProfiles();
		Arrays.sort(profiles);
		assertArrayEquals(new String[] {"prof1", "prof2"}, profiles);
	}

	@Test
	void testGetProfilesForKey() {
		Props p = new Props();

		p.load("zorg<prof2>=zero\n" +
				"foo=one\n" +
				"bar=two\n" +
				"[foo<prof1>]\n" +
				"info=zero\n" +
				"info2=zero2");

		String[] profiles = p.getProfilesFor("zorg");

		assertEquals(1, profiles.length);
		assertEquals("prof2", profiles[0]);

		profiles = p.getProfilesFor("zor*");

		assertEquals(1, profiles.length);
		assertEquals("prof2", profiles[0]);

		profiles = p.getProfilesFor("foo");
		assertEquals(0, profiles.length);

		profiles = p.getProfilesFor("foo.*");
		assertEquals(1, profiles.length);
		assertEquals("prof1", profiles[0]);

		profiles = p.getProfilesFor("foo*");
		assertEquals(1, profiles.length);
		assertEquals("prof1", profiles[0]);
	}

	@Test
	void testChangeActiveProfile() {
		Props p = new Props();

		p.load("foo=one\n" +
				"bar=two\n" +
				"foo<prof1>=aaa\n" +
				"foo<prof2>=bbb\n");

		p.setActiveProfiles("prof1");
		assertEquals("aaa", p.getValue("foo"));

		p.setActiveProfiles("prof2");
		assertEquals("bbb", p.getValue("foo"));
	}

	@Test
	void testWeirdKey() {
		Props p = new Props();

		p.load("org.jodd.Foo@Bar=one\n" +
				"org.jodd.Foo@*Bar=two\n" +
				"org.jodd.Foo@*Bar\\#me=three\n");

		assertEquals("one", p.getValue("org.jodd.Foo@Bar"));
		assertEquals("two", p.getValue("org.jodd.Foo@*Bar"));
		assertEquals("three", p.getValue("org.jodd.Foo@*Bar#me"));
	}

	@Test
	void testMultipleProfilesAtOnce() {
		Props p = new Props();
		p.load(
				"foo.one=111\n" +
				"foo.one<pr1>=111222\n" +
				"foo.one<pr2>=111222333\n"
		);

		p.setActiveProfiles(null);
		assertEquals("111", p.getValue("foo.one"));

		p.setActiveProfiles("pr1");
		assertEquals("111222", p.getValue("foo.one"));

		p.setActiveProfiles("pr2");
		assertEquals("111222333", p.getValue("foo.one"));

		p.setActiveProfiles("pr1", "pr2");
		assertEquals("111222", p.getValue("foo.one"));

		p.setActiveProfiles("pr2", "pr1");
		assertEquals("111222333", p.getValue("foo.one"));
	}

	@Test
	void testMacrosAndProfiles() {
		Props p = new Props();
		p.load(
				"one=111\n" +
				"one<pr1>=111222\n" +
				"one<pr2>=111222333\n" +
				"wow=${one}"
		);

		p.setActiveProfiles(null);
		assertEquals("111", p.getValue("wow"));

		p.setActiveProfiles("pr1");
		assertEquals("111222", p.getValue("wow"));

		p.setActiveProfiles("pr2");
		assertEquals("111222333", p.getValue("wow"));

		p.setActiveProfiles("pr1", "pr2");
		assertEquals("111222", p.getValue("wow"));
	}

	@Test
	void testMacrosAndProfilesAsBefore() {
		Props p = new Props();
		p.load(
				"one=111\n" +
				"one<pr1>=111222\n" +
				"one<pr2>=111222333\n" +
				"wow=${one}"
		);

		p.setActiveProfiles(null);
		assertEquals("111", p.getValue("wow"));

		p.setActiveProfiles("pr1");
		assertEquals("111222", p.getValue("wow"));

		p.setActiveProfiles("pr2");
		assertEquals("111222333", p.getValue("wow"));

		p.setActiveProfiles("pr1", "pr2");
		assertEquals("111222", p.getValue("wow"));

		// wow needs to be defined in a profile to get the profile value in macro
		// NOT ANYMORE!

		p = new Props();
		p.load(
				"one=111\n" +
				"one<pr1>=111222\n" +
				"one<pr2>=111222333\n" +
				"wow<pr1>=${one}"
		);

		p.setActiveProfiles(null);
		assertEquals(null, p.getValue("wow"));

		p.setActiveProfiles("pr1");
		assertEquals("111222", p.getValue("wow"));

		p.setActiveProfiles("pr2");
		assertEquals(null, p.getValue("wow"));

		p.setActiveProfiles("pr1", "pr2");
		assertEquals("111222", p.getValue("wow"));


		p = new Props();
		p.load(
				"one=111\n" +
				"one<pr1>=111222\n" +
				"one<pr2>=111222333\n" +
				"wow<pr1><pr2>=${one}"
		);

		p.setActiveProfiles(null);
		assertEquals(null, p.getValue("wow"));

		p.setActiveProfiles("pr1");
		assertEquals("111222", p.getValue("wow"));

		p.setActiveProfiles("pr2");
		assertEquals("111222333", p.getValue("wow"));

		p.setActiveProfiles("pr1", "pr2");
		assertEquals("111222", p.getValue("wow"));
	}

	@Test
	void testCopy() {
		Props p = new Props();

		p.load("foo.one=111\n" +
				"fig.two=222\n" +
				"bar <= foo, fig");

		assertEquals("111", p.getValue("foo.one"));
		assertEquals("222", p.getValue("fig.two"));
		assertEquals("111", p.getValue("bar.one"));
		assertEquals("222", p.getValue("bar.two"));
	}

	@Test
	void testCopyWithProfiles() {
		Props p = new Props();
		p.load(
				"foo.one=111\n" +
				"foo.one<pr1>=111111\n" +
				"foo.one<pr2>=111111111\n" +
				"fig.two<pr2>=222\n" +
				"bar <= foo, fig");

		assertEquals("111", p.getValue("foo.one"));
		assertEquals(null, p.getValue("fig.two"));
		assertEquals("111", p.getValue("bar.one"));
		assertEquals(null, p.getValue("bar.two"));

		p = new Props();
		p.load(
				"foo.one=111\n" +
				"foo.one<pr1>=111111\n" +
				"foo.one<pr2>=111111111\n" +
				"fig.two<pr2>=222\n" +
				"bar<pr1> <= foo, fig");

		p.setActiveProfiles("pr1");
		
		assertEquals("111111", p.getValue("foo.one"));
		assertEquals(null, p.getValue("fig.two"));
		assertEquals("111111", p.getValue("bar.one"));
		assertEquals(null, p.getValue("bar.two"));

		p = new Props();
		p.load(
				"foo.one=111\n" +
				"foo.one<pr1>=111111\n" +
				"foo.one<pr2>=111111111\n" +
				"fig.two<pr2>=222\n" +
				"bar<pr1><pr2> <= foo, fig\n"
		);

		p.setActiveProfiles("pr1", "pr2");

		assertEquals("111111", p.getValue("foo.one"));
		assertEquals("222", p.getValue("fig.two"));
		assertEquals("111111", p.getValue("bar.one"));
		assertEquals("222", p.getValue("bar.two"));
	}

	@Test
	void testCopyEmpty() {
		Props p = new Props();

		p.load("foo.one=111\n" +
				"fig.two=222\n" +
				"[bar]\n" +
				"<= foo, fig");

		assertEquals("111", p.getValue("foo.one"));
		assertEquals("222", p.getValue("fig.two"));
		assertEquals("111", p.getValue("bar.one"));
		assertEquals("222", p.getValue("bar.two"));
	}

	@Test
	void testIssue78() {
		String data =
				"@profiles=o\n" +
				"\n" +
				"prefix<o> = is Good\n" +
				"prefix<l> = is very Good\n" +
				"\n" +
				"[user]\n" +
				"name = jodd ${prefix}";

		Props props = new Props();
		props.load(data);

		assertEquals("jodd is Good", props.getValue("user.name"));
	}

	@Test
	void testAdditionalEquals() {
		String data =
				"account-dn = cn=accountname,ou=users,o=organization\n";

		Props props = new Props();
		props.load(data);

		assertEquals("cn=accountname,ou=users,o=organization", props.getValue("account-dn"));
	}


	@Test
	void testDifferentLineEndings() {
		Props props = new Props();
		props.setIgnorePrefixWhitespacesOnNewLine(true);
		props.load("text=line1\\\n   line2\\\r\n   line3\\\r   line4");

		assertEquals("line1line2line3line4", props.getValue("text"));

		props = new Props();
		props.setIgnorePrefixWhitespacesOnNewLine(false);
		props.load("text=line1\\\n   line2\\\r\n   line3\\\r   line4");

		assertEquals("line1   line2   line3   line4", props.getValue("text"));

		props = new Props();
		props.setIgnorePrefixWhitespacesOnNewLine(false);
		props.setEscapeNewLineValue("|");
		props.load("text=line1\\\n   line2\\\r\n   line3\\\r   line4");

		assertEquals("line1|   line2|   line3|   line4", props.getValue("text"));
	}

	@Test
	void testLoad_with_file_props() throws IOException, URISyntaxException {
		final File src = readDataToFile("test2.props");
		final Props actual = new Props().load(src);

		// asserts
		assertEquals(3, actual.countTotalProperties());
	}

	@Test
	void testLoad_with_file_properties() throws IOException, URISyntaxException {
		final File src = readDataToFile("test.properties");
		final Props actual = new Props().load(src);

		// asserts
		assertEquals(3, actual.countTotalProperties());
	}

	@Test
	void testLoad_with_file_and_encoding() throws IOException, URISyntaxException {
		final File src = readDataToFile("test2.props");
		final Props actual = new Props().load(src, StandardCharsets.UTF_8.name());

		// asserts
		assertEquals(3, actual.countTotalProperties());
	}

	@Test
	void testLoad_with_inputstream() throws IOException {
		try (final InputStream is = readDataToInputstream("test2.props")) {
			final Props actual = new Props().load(is);
			// asserts
			assertEquals(3, actual.countTotalProperties());
		}
	}

	@Nested
	@DisplayName("test for Props#getXXXValue() - methods")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS) // needed because annotation MethodSource requires static method without that
	class GetXXXValue {

		Props props;

		@BeforeEach
		void beforeEach() {
			props = new Props();
			Map<String, String> map = new HashMap<>();

			// test data
			map.put("string_jodd", "jodd");
			map.put("boolean_true", "true");
			map.put("boolean_false", "false");
			map.put("integer_0", "0");
			map.put("integer_1234567890", "1234567890");
			map.put("integer_-45232", "-45232");
			map.put("long_0", "0");
			map.put("long_1234567890", "1234567890");
			map.put("long_-2789899", "-2789899");
			map.put("double_1234567890_12", "1234567890.12");
			map.put("double_12345678903333_34", "12345678903333.34");
			map.put("double_-43478954.44", "-43478954.44");

			props.load(map);
		}

		@ParameterizedTest (name = "{index} - Props#{1}(''{2}'') == {0}")
		@MethodSource(value = "testdata")
		void testGetXXXValue(final Object expected, final String methodName, final String key) throws Exception {

			Method method = props.getClass().getDeclaredMethod(methodName, String.class);
			final Object actual = method.invoke(props, key);

			// asserts
			assertEquals(expected, actual);
		}

		@ParameterizedTest (name = "{index} - Props#{1}(''{2}'', null) == {0}")
		@MethodSource(value = "testdata")
		void testGetXXXValue_WithProfile(final Object expected, final String methodName, final String key) throws Exception {

			Method method = props.getClass().getDeclaredMethod(methodName, String.class, String[].class);
			final Object actual = method.invoke(props, key, (String[])null);

			// asserts
			assertEquals(expected, actual);
		}

		@ParameterizedTest (name = "{index} - Props#{1}(''{2}'', {3}, ''{4}'') == {0}")
		@MethodSource(value = "testdata_for_defaultvalues_and_profiles_test")
		void testGetXXXValue_WithDefaultValueAndProfiles(final Object expected, final String methodName, final String key, final Class clazzDefaultValue, final String[] profiles) throws Exception {
			Method method = props.getClass().getDeclaredMethod(methodName, String.class, expected.getClass(), String[].class);
			final Object actual = method.invoke(props, key, expected, new String[] {"jodd"});

			// asserts
			assertEquals(expected, actual);
		}

		@ParameterizedTest (name = "{index} - Props#{1}(''{2}'', {3}) == {0}")
		@MethodSource(value = "testdata_for_defaultvalues_test")
		void testGetXXXValue_WithDefaultValue(final Object expected, final String methodName, final String key, final Class clazzDefaultValue) throws Exception {
			Method method = props.getClass().getDeclaredMethod(methodName, String.class, expected.getClass());
			final Object actual = method.invoke(props, key, expected);

			// asserts
			assertEquals(expected, actual);
		}

		private Stream<Arguments> testdata() {
			return Stream.of(
					// getValue
					Arguments.of("jodd", "getValue", "string_jodd"),
					Arguments.of(null, "getValue", "unknown_key"),
					// getBooleanValue
					Arguments.of(Boolean.TRUE, "getBooleanValue", "boolean_true"),
					Arguments.of(Boolean.FALSE, "getBooleanValue", "boolean_false"),
					Arguments.of(null, "getBooleanValue", "unknown_key"),
					// getIntegerValue
					Arguments.of(0, "getIntegerValue", "integer_0"),
					Arguments.of(1234567890, "getIntegerValue", "integer_1234567890"),
					Arguments.of(-45232, "getIntegerValue", "integer_-45232"),
					Arguments.of(null, "getIntegerValue", "unknown_key"),
					// getLongValue
					Arguments.of(0L, "getLongValue", "long_0"),
					Arguments.of(1234567890L, "getLongValue", "long_1234567890"),
					Arguments.of(-2789899L, "getLongValue", "long_-2789899"),
					Arguments.of(null, "getLongValue", "unknown_key"),
					// getDoubleValue
					Arguments.of(1234567890.12D, "getDoubleValue", "double_1234567890_12"),
					Arguments.of(12345678903333.34D, "getDoubleValue", "double_12345678903333_34"),
					Arguments.of(-43478954.44D, "getDoubleValue", "double_-43478954.44"),
					Arguments.of(null, "getDoubleValue", "unknown_key")
			);
		}

		private Stream<Arguments> testdata_for_defaultvalues_and_profiles_test() {
			final String an_unknown_key = "this_is_definitely_an_unknown_key_for_test_in_props_test";
			final String[] profiles = new String[] {"jodd", "db"};
			return Stream.of(
					// getBooleanValue
					Arguments.of(Boolean.FALSE, "getBooleanValue", an_unknown_key, Boolean.class, profiles),
					Arguments.of(Boolean.TRUE, "getBooleanValue", "boolean_true", Boolean.class, profiles),
					// getIntegerValue
					Arguments.of(-45232, "getIntegerValue", an_unknown_key, Integer.class, profiles),
					Arguments.of(0, "getIntegerValue", "integer_0", Integer.class, profiles),
					// getLongValue
					Arguments.of(1234567890L, "getLongValue", an_unknown_key, Long.class, profiles),
					Arguments.of(-2789899L, "getLongValue", "long_-2789899", Long.class, profiles),
					// getDoubleValue
					Arguments.of(-888.541D, "getDoubleValue", an_unknown_key, Double.class, profiles),
					Arguments.of(1234567890.12, "getDoubleValue", "double_1234567890_12", Double.class, profiles)
			);
		}

		private Stream<Arguments> testdata_for_defaultvalues_test() {
			final String an_unknown_key = "this_is_definitely_an_unknown_key_for_test_in_props_test";
			return Stream.of(
					// getBooleanValue
					Arguments.of(Boolean.FALSE, "getBooleanValue", an_unknown_key, Boolean.class),
					Arguments.of(Boolean.TRUE, "getBooleanValue", "boolean_true", Boolean.class),
					// getIntegerValue
					Arguments.of(-45232, "getIntegerValue", an_unknown_key, Integer.class),
					Arguments.of(0, "getIntegerValue", "integer_0", Integer.class),
					// getLongValue
					Arguments.of(0L, "getLongValue",  an_unknown_key, Long.class),
					Arguments.of(-2789899L, "getLongValue", "long_-2789899", Long.class),
					// getDoubleValue
					Arguments.of(-888.541D, "getDoubleValue", an_unknown_key, Double.class),
					Arguments.of(1234567890.12, "getDoubleValue", "double_1234567890_12", Double.class),
					// getValueOrDefault
					Arguments.of("jodd", "getValueOrDefault", an_unknown_key, String.class),
					Arguments.of("jodd", "getValueOrDefault", "string_jodd", String.class)
					);
		}

	}
}
