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

public abstract class BookActionTestBase {

	@Test
	public void testBookGet() {
		HttpResponse response;
		response = HttpRequest.get("localhost:8173/book/123").send();

		assertEquals("MyBook: 123:Songs of Distant Earth.", response.bodyText().trim());
	}

	@Test
	public void testBookPost() {
		HttpResponse response;
		response = HttpRequest.post("localhost:8173/book/123").send();

		assertEquals("NewBook: 123:Songs of Distant Earth.", response.bodyText().trim());
	}

	@Test
	public void testBookPut() {
		HttpResponse response;
		response = HttpRequest.put("localhost:8173/book/123").send();

		assertEquals("OldBook: 123:Songs of Distant Earth.", response.bodyText().trim());
	}

	@Test
	public void testBookPartial() {
		HttpResponse response = HttpRequest.put("localhost:8173/bookPartial.hello.html")
				.query("book.iban", "123123123")
				.query("book.foo", "not used")
				.send();

		assertEquals("Hi123123123", response.bodyText().trim());
	}
}
