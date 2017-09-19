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

package jodd.util;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

import static jodd.util.URLCoder.*;
import static jodd.util.URLDecoder.decode;
import static jodd.util.URLDecoder.decodeQuery;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class URLCoderTest {
	
	@Test
	public void testEncodeScheme() {
		assertEquals("foobar+-.", encodeScheme("foobar+-."));
		assertEquals("foo%20bar", encodeScheme("foo bar"));
	}

	@Test
	public void testEncodeUserInfo() {
		assertEquals("foobar:", encodeUserInfo("foobar:"));
		assertEquals("foo%20bar", encodeUserInfo("foo bar"));
	}

	@Test
	public void testEncodeHost() {
		assertEquals("foobar", encodeHost("foobar"));
		assertEquals("foo%20bar", encodeHost("foo bar"));
	}

	@Test
	public void testEncodePort() {
		assertEquals("80", encodePort("80"));
	}

	@Test
	public void testEncodePath() {
		assertEquals("/foo/bar", encodePath("/foo/bar"));
		assertEquals("/foo%20bar", encodePath("/foo bar"));
		assertEquals("/Z%C3%BCrich", encodePath("/Z\u00fcrich"));
	}

	@Test
	public void testEncodePathSegment() {
		assertEquals("foobar", encodePathSegment("foobar"));
		assertEquals("%2Ffoo%2Fbar", encodePathSegment("/foo/bar"));
	}

	@Test
	public void testEncodeQuery() {
		assertEquals("foobar", encodeQuery("foobar"));
		assertEquals("foo%20bar", encodeQuery("foo bar"));
		assertEquals("foobar/+", encodeQuery("foobar/+"));
		assertEquals("T%C5%8Dky%C5%8D", encodeQuery("T\u014dky\u014d"));
		assertEquals("foo&bar", encodeQuery("foo&bar"));
		assertEquals("foo=one&bar=two", encodeQuery("foo=one&bar=two"));
	}

	@Test
	public void testEncodeQueryParam() {
		assertEquals("foobar", encodeQueryParam("foobar"));
		assertEquals("foo%20bar", encodeQueryParam("foo bar"));
		assertEquals("foobar/+", encodeQuery("foobar/+"));
		assertEquals("foo%26bar", encodeQueryParam("foo&bar"));
		assertEquals("foo%3Dbar", encodeQueryParam("foo=bar"));
		assertEquals("foo@bar", encodeQueryParam("foo@bar"));
		assertEquals("foo%3Done%26bar%3Dtwo", encodeQueryParam("foo=one&bar=two"));
	}

	@Test
	public void testEncodeFragment() {
		assertEquals("foobar", encodeFragment("foobar"));
		assertEquals("foo%20bar", encodeFragment("foo bar"));
		assertEquals("foobar/", encodeFragment("foobar/"));
	}

	@Test
	public void testDecode() {
		assertEquals("", decode(""));
		assertEquals("foobar", decode("foobar"));
		assertEquals("foo bar", decode("foo%20bar"));
		assertEquals("foo+bar", decode("foo%2bbar"));
		assertEquals("T\u014dky\u014d", decode("T%C5%8Dky%C5%8D"));
		assertEquals("/Z\u00fcrich", decode("/Z%C3%BCrich"));
		assertEquals("T\u014dky\u014d", decode("T\u014dky\u014d"));
		assertEquals("foo+bar", decode("foo+bar"));
		assertEquals("foo bar", decodeQuery("foo+bar"));
	}

	@Test
	public void testEncodeUri() {
		assertEquals("http://www.ietf.org/rfc/rfc3986.txt",
				encodeUri("http://www.ietf.org/rfc/rfc3986.txt"));
		assertEquals("https://www.ietf.org/rfc/rfc3986.txt",
				encodeUri("https://www.ietf.org/rfc/rfc3986.txt"));
		assertEquals("http://www.google.com/?q=Z%C3%BCrich",
				encodeUri("http://www.google.com/?q=Z\u00fcrich"));
		assertEquals(
				"http://arjen:foobar@java.sun.com:80/javase/6/docs/api/java/util/BitSet.html?foo=bar#and(java.util.BitSet)",
				encodeUri("http://arjen:foobar@java.sun.com:80/javase/6/docs/api/java/util/BitSet.html?foo=bar#and(java.util.BitSet)"));
		assertEquals("http://java.sun.com/j2se/1.3/",
				encodeUri("http://java.sun.com/j2se/1.3/"));
		assertEquals("docs/guide/collections/designfaq.html#28",
				encodeUri("docs/guide/collections/designfaq.html#28"));
		assertEquals("../../../demo/jfc/SwingSet2/src/SwingSet2.java",
				encodeUri("../../../demo/jfc/SwingSet2/src/SwingSet2.java"));
		assertEquals("file:///~/calendar", encodeUri("file:///~/calendar"));
		assertEquals("http://example.com/query=foo@bar",
				encodeUri("http://example.com/query=foo@bar"));


		assertEquals("http://example.org?format=json&url=http://another.com?foo=bar",
				encodeUri("http://example.org?format=json&url=http://another.com?foo=bar"));
	}

	@Test
	public void testEncodeHttpUrl() {
		assertEquals("http://www.ietf.org/rfc/rfc3986.txt",
				encodeHttpUrl("http://www.ietf.org/rfc/rfc3986.txt"));
		assertEquals("https://www.ietf.org/rfc/rfc3986.txt",
				encodeHttpUrl("https://www.ietf.org/rfc/rfc3986.txt"));
		assertEquals("http://www.google.com/?q=Z%C3%BCrich",
				encodeHttpUrl("http://www.google.com/?q=Z\u00fcrich"));
		assertEquals("http://ws.geonames.org/searchJSON?q=T%C5%8Dky%C5%8D&style=FULL&maxRows=300",
				encodeHttpUrl("http://ws.geonames.org/searchJSON?q=T\u014dky\u014d&style=FULL&maxRows=300"));
		assertEquals(
				"http://arjen:foobar@java.sun.com:80/javase/6/docs/api/java/util/BitSet.html?foo=bar",
				encodeHttpUrl("http://arjen:foobar@java.sun.com:80/javase/6/docs/api/java/util/BitSet.html?foo=bar"));
		assertEquals("http://search.twitter.com/search.atom?q=%23avatar",
				encodeHttpUrl("http://search.twitter.com/search.atom?q=#avatar"));
		assertEquals("http://java.sun.com/j2se/1.3/",
				encodeHttpUrl("http://java.sun.com/j2se/1.3/"));
		assertEquals("http://example.com/query=foo@bar",
				encodeHttpUrl("http://example.com/query=foo@bar"));
	}

	@Test
	public void encodeHttpUrlMail() {
		assertThrows(IllegalArgumentException.class, () -> encodeHttpUrl("mailto:java-net@java.sun.com"));
	}

	@Test
	public void testEncodeUrl() {
		assertEquals("/aaa", URLCoder.encodeUri("/aaa"));
		assertEquals("/aaa?", URLCoder.encodeUri("/aaa?"));
		assertEquals("/aaa?b", URLCoder.encodeUri("/aaa?b"));
		assertEquals("/aaa?b=", URLCoder.encodeUri("/aaa?b="));
		assertEquals("/aaa?b=c", URLCoder.encodeUri("/aaa?b=c"));
		assertEquals("/aaa?b=%20c", URLCoder.encodeUri("/aaa?b= c"));
		assertEquals("/aaa?b=%20c&", URLCoder.encodeUri("/aaa?b= c&"));
		assertEquals("/aaa?b=%20c&dd", URLCoder.encodeUri("/aaa?b= c&dd"));
		assertEquals("/aaa?b=%20c&dd=", URLCoder.encodeUri("/aaa?b= c&dd="));
		assertEquals("/aaa?b=%20%20c&dd==", URLCoder.encodeUri("/aaa?b=  c&dd=="));
		assertEquals("?data=The%20string%20%C3%BC@foo-bar", URLCoder.encodeUri("?data=The string ü@foo-bar"));
	}

	@Test
	public void testQuerySimple() throws UnsupportedEncodingException {
		assertEquals("%C5%BD%C4%8C%C4%86", encodeQueryParam("ŽČĆ"));    // utf8
		assertEquals("@-._~%2B%20", encodeQueryParam("@-._~+ "));
		assertEquals("http://jodd.org/download?param=I%20love%20Jodd+Java", URLCoder.encodeHttpUrl("http://jodd.org/download?param=I love Jodd+Java"));
		assertEquals("http://jodd.org?param=java&jodd", URLCoder.encodeHttpUrl("http://jodd.org?param=java&jodd"));    // this is ambiguous
	}

	@Test
	public void testUrlBuilder() {
		assertEquals("http://jodd.org", URLCoder.build("http://jodd.org").toString());
		assertEquals("http://jodd.org?param=jodd%26java", URLCoder.build("http://jodd.org").queryParam("param", "jodd&java").toString());
		assertEquals("http://jodd.org?pa%20ram=jodd%2Bjava", URLCoder.build("http://jodd.org").queryParam("pa ram", "jodd+java").toString());
		assertEquals("/foo?foo=one&bar=two", URLCoder.build("/foo").queryParam("foo", "one").queryParam("bar", "two").toString());
	}

}
