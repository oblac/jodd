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

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpHeaderTest {

	@Test
	void testSettingHostsHeader_changeWithSet() {
		final HttpRequest httpRequest = HttpRequest.post("jodd.site");

		assertEquals("jodd.site", httpRequest.host());
		// calling toString as it will set the HEADER
		assertTrue(httpRequest.toString().contains("jodd.site"));

		assertEquals("jodd.site", httpRequest.header(HttpRequest.HEADER_HOST));

		// change
		httpRequest.set("oblac.rs");

		// is the header changed? first check the header
		assertEquals("oblac.rs", httpRequest.host());
		assertEquals("oblac.rs", httpRequest.header(HttpRequest.HEADER_HOST));
		// the regenerated request should work
		assertTrue(httpRequest.toString().contains("oblac.rs"));
		// after the generation
		assertEquals("oblac.rs", httpRequest.header(HttpRequest.HEADER_HOST));
	}

	@Test
	void testSettingHostsHeader_changeWithHost() {
		final HttpRequest httpRequest = HttpRequest.post("jodd.site");

		assertEquals("jodd.site", httpRequest.host());
		// calling toString as it will set the HEADER
		assertTrue(httpRequest.toString().contains("jodd.site"));

		assertEquals("jodd.site", httpRequest.header(HttpRequest.HEADER_HOST));

		// change
		httpRequest.host("oblac.rs");

		// is the header changed? first check the header
		assertEquals("oblac.rs", httpRequest.host());
		assertEquals("oblac.rs", httpRequest.header(HttpRequest.HEADER_HOST));
		// the regenerated request should work
		assertTrue(httpRequest.toString().contains("oblac.rs"));
		// after the generation
		assertEquals("oblac.rs", httpRequest.header(HttpRequest.HEADER_HOST));
	}

}
