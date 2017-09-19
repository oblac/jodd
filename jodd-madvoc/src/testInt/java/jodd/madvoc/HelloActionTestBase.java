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
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class HelloActionTestBase {

	@Test
	public void testHelloAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.html").send();
		assertEquals("hello", response.bodyText().trim());

		response = HttpRequest.get("localhost:8173/pac/hello.html").send();
		assertEquals("HELLO", response.bodyText().trim());
	}

	@Test
	public void testHelloWorldAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.world.html?name=Jupiter&data=3").send();
		assertEquals("Hello world planet Jupiter and Universe 3", response.bodyText().trim());
	}

	@Test
	public void testHelloPlanetAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.planet.html?name=Jupiter").send();
		assertEquals("Hello planet Jupiter.", response.bodyText().trim());
	}

	@Test
	public void testHelloBeanAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.bean.html?p.name=Jupiter&p.data=3").send();
		assertEquals("Person{name='Jupiter', data=3}", response.bodyText().trim());
	}

	@Test
	public void testHelloDirectAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.direct.html").send();
		assertEquals("Direct stream output", response.bodyText().trim());
	}

	@Test
	public void testHelloReqReqAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.reqreq.html?hey=YOU").body("Jodd").send();
		assertEquals("Hello YOU GET Jodd", response.bodyText().trim());
	}

	@Test
	public void testHelloNoJspAction() {
		HttpResponse response = HttpRequest.get("localhost:8173/nohello.nojsp.html").send();
		assertEquals(404, response.statusCode());
		assertTrue(response.bodyText().contains("/nohello.nojsp.html"));
	}

	@Test
	public void testChain() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.chain.html?chain=7").send();
		assertEquals("chain:9", response.bodyText().trim());
	}

	@Test
	public void testMany() {
		HttpResponse response = HttpRequest.get(
				"localhost:8173/hello.many.html?" +
				"ppp[0].name=Aaa&ppp[0].data=1&ppp[1].name=Bbb&ppp[1].data=2&ppp[2].name=Ccc&ppp[2].data=3").send();
		assertEquals(
				"0 Aaa-1\n" +
				"1 Bbb-2\n" +
				"2 Ccc-3\n" +
				"0 Aaa-1\n" +
				"1 Bbb-2\n" +
				"2 Ccc-3\n" +
				"{0=Person{name='Aaa', data=1}, 1=Person{name='Bbb', data=2}, 2=Person{name='Ccc', data=3}}", response.bodyText().trim());
	}

	@Test
	public void testBackBack() {
		HttpResponse response = HttpRequest.get("localhost:8173/hello.backback.html").send();
		assertEquals("default.big", response.bodyText().trim());
	}

}
