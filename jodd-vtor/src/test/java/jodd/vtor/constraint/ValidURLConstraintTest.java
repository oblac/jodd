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

package jodd.vtor.constraint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// see: https://mathiasbynens.be/demo/url-regex
public class ValidURLConstraintTest {

	public static boolean check(String url) {
		return ValidURLConstraint.validate(url);
	}

	public static final String[] SHOULD_MATCH = {
		"http://foo.com/blah_blah",
		"http://foo.com/blah_blah/",
		"http://foo.com/blah_blah_(wikipedia)",
		"http://foo.com/blah_blah_(wikipedia)_(again)",
		"http://www.example.com/wpstyle/?p=364",
		"https://www.example.com/foo/?bar=baz&inga=42&quux",
		"http://✪df.ws/123",
		"http://userid:password@example.com:8080",
		"http://userid:password@example.com:8080/",
		"http://userid@example.com",
		"http://userid@example.com/",
		"http://userid@example.com:8080",
		"http://userid@example.com:8080/",
		"http://userid:password@example.com",
		"http://userid:password@example.com/",
		"http://142.42.1.1/",
		"http://142.42.1.1:8080/",
		"http://➡.ws/䨹",
		"http://⌘.ws",
		"http://⌘.ws/",
		"http://foo.com/blah_(wikipedia)#cite-1",
		"http://foo.com/blah_(wikipedia)_blah#cite-1",
		"http://foo.com/unicode_(✪)_in_parens",
		"http://foo.com/(something)?after=parens",
		"http://☺.damowmow.com/",
		"http://code.google.com/events/#&product=browser",
		"http://j.mp",
		"ftp://foo.bar/baz",
		"http://foo.bar/?q=Test%20URL-encoded%20stuff",
		"http://مثال.إختبار",
		"http://例子.测试",
		"http://उदाहरण.परीक्षा",
		"http://-.~_!$&'()*+,;=:%40:80%2f::::::@example.com",
		"http://1337.net",
		"http://a.b-c.de",
		"http://223.255.255.254",
	};

	public static final String[] SHOULD_NOT_MATCH = {
		"http://",
		"http://.",
		"http://..",
		"http://../",
		"http://?",
		"http://??",
		"http://??/",
		"http://#",
		"http://##",
		"http://##/",
		"http://foo.bar?q=Spaces should be encoded",
		"//",
		"//a",
		"///a",
		"///",
		"http:///a",
		"foo.com",
		"rdar://1234",
		"h://test",
		"http:// shouldfail.com",
		":// should fail",
		"http://foo.bar/foo(bar)baz quux",
		"ftps://foo.bar/",
		"http://-error-.invalid/",
		//"http://a.b--c.de/",
		"http://-a.b.co",
		"http://a.b-.co",
		"http://0.0.0.0",
		"http://10.1.1.0",
		"http://10.1.1.255",
		"http://224.1.1.1",
		"http://1.1.1.1.1",
		"http://123.123.123",
		"http://3628126748",
		"http://.www.foo.bar/",
		//"http://www.foo.bar./",
		"http://.www.foo.bar./",
		"http://10.1.1.1",
	};

	@Test
	public void testValidUrls() {
		for (String url : SHOULD_MATCH) {
			assertTrue(check(url));
		}
	}

	@Test
	public void testInvalidUrls() {
		for (String url : SHOULD_NOT_MATCH) {
			assertFalse(check(url));
		}
	}

}
