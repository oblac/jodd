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

package jodd.lagarto.form;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class FormTextTestBase {

	private static final String TEXT_RESULT = "<input name=\"iname\" type=\"text\" value=\"foo\">";

	@Test
	public void testFormTagTextGet() {
		HttpResponse response = HttpRequest
				.get("localhost:8173/text.jsp")
				.query("iname", "foo")
				.send();

		assertEquals(TEXT_RESULT, response.bodyText().trim());
	}

	@Test
	public void testFormTagTextGetWithValue() {
		HttpResponse response = HttpRequest
				.get("localhost:8173/text2.jsp")
				.query("iname", "foo")
				.send();

		assertEquals(TEXT_RESULT, response.bodyText().trim());
	}

	@Test
	public void testFormTagTextPost() {
		HttpResponse response = HttpRequest
				.post("localhost:8173/text.jsp")
				.form("iname", "foo")
				.send();

		assertEquals(TEXT_RESULT, response.bodyText().trim());
	}

	@Test
	public void testFormTagTextPostMulti() {
		HttpResponse response = HttpRequest
				.post("localhost:8173/text.jsp")
				.form("iname", "foo")
				.multipart(true)
				.send();

		assertEquals(TEXT_RESULT, response.bodyText().trim());
	}

	@Test
	public void testFormTagDuplicateNames() {
		HttpResponse response = HttpRequest
				.post("localhost:8173/text3.jsp")
				.form("cc", "one")
				.form("cc", "two")
				.send();

		System.out.println(response.bodyText().trim());

		assertEquals("<input type=\"text\" name=\"cc\" id=\"cc1\" value=\"one\"/>\n" +
			"<input type=\"text\" name=\"cc\" id=\"cc2\" value=\"two\"/>\n" +
			"<input type=\"text\" name=\"cc\" id=\"cc3\"/>", response.bodyText().trim());
	}
}
