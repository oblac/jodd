// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.servlet;

import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class URLCoderTest extends TestCase {

	public void testQuerySimple() throws UnsupportedEncodingException {
		assertEquals(URLEncoder.encode("d a d a", "UTF-8"), URLCoder.encodeQuery("d a d a"));
		assertEquals(URLEncoder.encode("dšačdža", "UTF-8"), URLCoder.encodeQuery("dšačdža"));
		assertEquals(URLEncoder.encode("I love", "UTF-8"), URLCoder.encodeQuery("I love"));
		assertEquals(URLEncoder.encode("v+al ue", "UTF-8"), URLCoder.encodeQuery("v+al ue"));
		assertEquals("v%2Bal+ue", URLCoder.encodeQuery("v+al ue"));
		assertEquals("I+love", URLCoder.encodeQuery("I love"));
		assertEquals("%3A%2F%3F%23%5B%5D%40", URLCoder.encodeQuery(":/?#[]@"));
		assertEquals("%C5%BD%C4%8C%C4%86", URLCoder.encodeQuery("ŽČĆ"));	// utf8
		assertEquals("-._%7E%2B+", URLCoder.encodeQuery("-._~+ "));
		assertEquals("http://jodd.org/download?param=I+love+Jodd%2BJava", URLCoder.url("http://jodd.org/download?param=I love Jodd+Java"));
		assertEquals("http://jodd.org?param=java&jodd", URLCoder.url("http://jodd.org?param=java&jodd"));	// this is ambiguous
	}

	public void testQueryAll() throws UnsupportedEncodingException {
		for (char c = 0; c < Character.MAX_VALUE; c++) {
			String s = String.valueOf(c);
			assertEquals(URLEncoder.encode(s, "UTF-8"), URLCoder.encodeQuery(s));
		}
	}
	public void testPathAll() throws URISyntaxException {
		for (char c = 0; c < Character.MAX_VALUE; c++) {
			String s = new URI("a", "a", "/" + c, null, null).toString();
			assertEquals(s.substring(6), URLCoder.encodePath(String.valueOf(c)));
		}
	}

	public void testUrlBuilder() {
		assertEquals("http://jodd.org", URLCoder.build().path("http://jodd.org").toString());
		assertEquals("http://jodd.org?param=jodd%26java", URLCoder.build().path("http://jodd.org").param("param", "jodd&java").toString());
		assertEquals("http://jodd.org?param=jodd%26java", URLCoder.build().path("http://jodd.org").param("param=jodd&java").toString());
		assertEquals("http://jodd.org?pa+ram=jodd+%2B+java", URLCoder.build().path("http://jodd.org").param("pa ram", "jodd + java").toString());
	}

	public void testUriSpecialChar() throws URISyntaxException {
		assertEquals("http://user:pass@jodd.org/good%20stuff+funÀ/foo%C2%80À", URLCoder.url("http://user:pass@jodd.org/good stuff+funÀ/foo\u0080\u00C0"));
		assertEquals("http://user:pass@jodd.org/good%20stuff+funÀ/foo%C2%80À", new URI("http", "user:pass@jodd.org", "/good stuff+funÀ/foo\u0080\u00C0", null, null).toString());
	}

	public void testAll() throws UnsupportedEncodingException, URISyntaxException {
		assertEquals(
				new URI("http", null, "jodd.org", 80, "/f o+o.html", null, null).toString() + "?name=" + URLEncoder.encode("v+al ue", "UTF-8"),
				URLCoder.url("http://jodd.org:80/f o+o.html?name=v+al ue")
		);
		assertEquals(
				new URI("http", null, "jodd.org", 80, "/f o+o.html", null, null).toString() + "?name=" + URLEncoder.encode("v+al ue", "UTF-8"),
				URLCoder.url("http://jodd.org:80/f o+o.html?name=v+al ue")

		);

	}

}
