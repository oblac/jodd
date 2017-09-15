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

package jodd.http;

import jodd.util.StringPool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpUtilTest {

	@Test
	public void testNiceHeaderNames() {
		assertEquals("Content-Type", HttpUtil.prepareHeaderParameterName("conTent-tyPe"));
		assertEquals("ETag", HttpUtil.prepareHeaderParameterName("etag"));
	}

	@Test
	public void testMediaTypeAndParameters() {
		String contentType = "text/html";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals(null, HttpUtil.extractHeaderParameter(contentType, "charset", ';'));


		contentType = "text/html; charset=ISO-8859-4";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals("ISO-8859-4", HttpUtil.extractHeaderParameter(contentType, "charset", ';'));


		contentType = "text/html;charset=ISO-8859-4";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals("ISO-8859-4", HttpUtil.extractHeaderParameter(contentType, "charset", ';'));


		contentType = "text/html; pre=foo; charset=ISO-8859-4";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals("ISO-8859-4", HttpUtil.extractHeaderParameter(contentType, "charset", ';'));


		contentType = "text/html; pre=foo; charset=ISO-8859-4; post=bar";

		assertEquals("text/html", HttpUtil.extractMediaType(contentType));
		assertEquals("ISO-8859-4", HttpUtil.extractHeaderParameter(contentType, "charset", ';'));
		assertEquals("foo", HttpUtil.extractHeaderParameter(contentType, "pre", ';'));
		assertEquals(null, HttpUtil.extractHeaderParameter(contentType, "na", ';'));
	}

	@Test
	public void testDefaultPort() {
		HttpRequest request;

		request = HttpRequest.get("jodd.org");
		assertEquals("http", request.protocol());
		assertEquals(80, request.port());

		request = HttpRequest.get("jodd.org:80");
		assertEquals("http", request.protocol());
		assertEquals(80, request.port());

		request = HttpRequest.get("jodd.org:801");
		assertEquals("http", request.protocol());
		assertEquals(801, request.port());

		request = HttpRequest.get("http://jodd.org");
		assertEquals("http", request.protocol());
		assertEquals(80, request.port());

		request = HttpRequest.get("https://jodd.org");
		assertEquals("https", request.protocol());
		assertEquals(443, request.port());

		request = HttpRequest.get("https://jodd.org:8443");
		assertEquals("https", request.protocol());
		assertEquals(8443, request.port());
	}

	@Test
	public void testBuildQuery() {
		HttpMultiMap<String> map = HttpMultiMap.newCaseInsensitiveMap();

		assertEquals("", HttpUtil.buildQuery(map, StringPool.UTF_8));

		map.add("aaa", "one");
		assertEquals("aaa=one", HttpUtil.buildQuery(map, StringPool.UTF_8));

		map.add("bbb", "two");
		assertEquals("aaa=one&bbb=two", HttpUtil.buildQuery(map, StringPool.UTF_8));

		map.clear().add("ccc", null);
		assertEquals("ccc", HttpUtil.buildQuery(map, StringPool.UTF_8));

		map.add("ddd", "four");
		assertEquals("ccc&ddd=four", HttpUtil.buildQuery(map, StringPool.UTF_8));
	}

}
