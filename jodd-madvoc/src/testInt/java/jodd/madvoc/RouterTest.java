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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RouterTest {

	@BeforeClass
	public static void beforeClass() {
		MadvocTwoSuite.startTomcat();
	}

	@AfterClass
	public static void afterClass() {
		MadvocTwoSuite.stopTomcat();
	}

	@Test
	public void testRouterFile() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.html").send();
		assertEquals("hello", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/helloWorld.html?name=Jupiter&data=3").send();
		assertEquals("Hello world planet Jupiter and Universe 3", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/re/view/234").send();
		assertEquals("234", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/alpha.ciao.html").send();
		assertEquals("alpha.hello.jsp", response.bodyText().trim());
	}

	@Test
	public void testZigZag() {
		HttpResponse response = HttpRequest.get("localhost:8173/zigzag/123").send();
		assertEquals("zigzag 123", response.bodyText().trim());
	}

	@Test
	public void testUserWithRoute() {
		HttpResponse response = HttpRequest.get("localhost:8173/sys/user/456").send();
		assertEquals("Huh 456.", response.bodyText().trim());
	}

	@Test
	public void testBook() {
		HttpResponse response;
		response = HttpRequest.get("localhost:8173/book/123").send();

		assertEquals("MyBook: 123:Songs of Distant Earth.", response.bodyText().trim());
	}
}