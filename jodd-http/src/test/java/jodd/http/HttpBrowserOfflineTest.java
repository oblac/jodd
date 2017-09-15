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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpBrowserOfflineTest {

	@Test
	public void testDefaultParameters() {
		HttpBrowser httpBrowser = new HttpBrowser();
		httpBrowser.setDefaultHeader("aaa", "123");

		HttpRequest request = HttpRequest.get("foo.com");
		request.header("bbb", "987");

		httpBrowser.addDefaultHeaders(request);

		assertEquals(3, request.headerNames().size());
		assertEquals("123", request.header("aaa"));
		assertEquals("987", request.header("bbb"));
	}

	@Test
	public void testDefaultParametersOverwrite() {
		HttpBrowser httpBrowser = new HttpBrowser();
		httpBrowser.setDefaultHeader("aaa", "123");

		HttpRequest request = HttpRequest.get("foo.com");
		request.header("aaa", "987");

		httpBrowser.addDefaultHeaders(request);

		assertEquals(2, request.headerNames().size());
		assertEquals("987", request.header("aaa"));
	}
}
