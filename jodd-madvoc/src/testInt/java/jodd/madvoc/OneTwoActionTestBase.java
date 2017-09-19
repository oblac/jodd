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

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class OneTwoActionTestBase {

	@Test
	public void testOneRedirectAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/oneRedirect.html").send();
		assertEquals("", response.bodyText());
		assertEquals(302, response.statusCode());

		String redirectLocation = response.header("location");

		response = HttpRequest.get(redirectLocation).send();
		assertEquals("value = [333]", response.bodyText());
	}

	@Test
	public void testOneMoveAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/oneMove.html").send();
		assertEquals("", response.bodyText());
		assertEquals(302, response.statusCode());

		String redirectLocation = response.header("location");

		response = HttpRequest.get(redirectLocation).send();
		assertEquals("value = [777]", response.bodyText());
	}

	@Test
	public void testOneMoveGoAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/oneMove.go.html").send();
		assertEquals("", response.bodyText());
		assertEquals(302, response.statusCode());

		String redirectLocation = response.header("location");

		response = HttpRequest.get(redirectLocation).send();
		assertEquals("value = [888]", response.bodyText());
	}

	@Test
	public void testOneRedirectPermanentAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/oneRedirect.perm.html").send();
		assertEquals("", response.bodyText());
		assertEquals(301, response.statusCode());

		String redirectLocation = response.header("location");
		assertEquals("/two.html?value=444", redirectLocation);
	}

	@Test
	public void testOneRedirectPermanentAction2() {
		HttpResponse response = HttpRequest.get("localhost:8173/oneRedirect.permGoogle.html").send();
		assertEquals("", response.bodyText());
		assertEquals(301, response.statusCode());

		String redirectLocation = response.header("location");
		assertEquals("http://google.com", redirectLocation);
	}

}
