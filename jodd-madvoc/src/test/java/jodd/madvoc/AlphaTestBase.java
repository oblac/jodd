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

package jodd.madvoc;

import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AlphaTestBase {

	@Test
	public void testForwardTo() {
		HttpResponse response;

		response = HttpRequest.get("localhost:8173/alpha.html").send();
		assertEquals("alpha.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/alpha.hello.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/alpha.ciao.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/alpha.ciao2.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/alpha.hola.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/alpha.holahoopa.html").send();
		assertEquals("alpha.hola.jsp", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/alpha.home.html").send();
		assertEquals("hello", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/alpha.home2.html").send();
		assertEquals("Hello world", response.bodyText().trim());
	}

	@Test
	public void testRedirectTo() {
		HttpResponse response;
		HttpBrowser browser = new HttpBrowser();

		response = browser.sendRequest(HttpRequest.get("localhost:8173/alpha.red1.html"));
		assertEquals("alpha.jsp", response.bodyText().trim());

		response = browser.sendRequest(HttpRequest.get("localhost:8173/alpha.red2.html"));
		assertEquals("hello", response.bodyText().trim());

		response = browser.sendRequest(HttpRequest.get("localhost:8173/alpha.world.html"));
		assertEquals("Hello world planet Mars and Universe 173", response.bodyText().trim());

		response = browser.sendRequest(HttpRequest.get("localhost:8173/alpha.postme.html"));
		assertEquals("alpha.hello.jsp", response.bodyText().trim());
	}

	@Test
	public void testText() {
		HttpResponse response;

		response = HttpRequest.get("localhost:8173/alpha.txt.html").send();
		assertEquals("some text", response.bodyText().trim());
	}

	@Test
	public void testChain() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.chain.html?chain=7").send();
		assertEquals("chain:9", response.bodyText().trim());
	}

	@Test
	public void testNoResult() {
		HttpResponse response = HttpRequest.get("localhost:8173/alpha.noresult.html").send();
		assertEquals("noresult", response.bodyText().trim());
	}

}
