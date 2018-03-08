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

package jodd.json;

import jodd.json.fixtures.JsonParsers;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class LooseTest {

	@Test
	void testInvalidEscape() {
		JsonParsers.forEachParser(jsonParser -> {
			try {
				assertEquals("ABC\\D", jsonParser.parse("\"ABC\\D\""));
				fail("error");
			} catch (JsonException ignore) {
			}
			assertEquals("ABC\\D", jsonParser.looseMode(true).parse("\"ABC\\D\""));

			//		Map<String, Object> map = new JsonParser().looseMode(true).parse("{\"foo\": \"bar\\\"}");
			//		assertEquals(1, map.size());
			//		assertEquals("bar\\", map.get("foo"));
		});
	}

	@Test
	void testQuotes() {
		JsonParsers.forEachParser(jsonParser -> {
			try {
				assertEquals("ABC", jsonParser.parse("'ABC'"));
				fail("error");
			} catch (JsonException ignore) {
			}

			assertEquals("ABC", jsonParser.looseMode(true).parse("'ABC'"));
			assertEquals("AB'C", jsonParser.looseMode(true).parse("'AB\\'C'"));

			Map<String, Object> map = jsonParser.looseMode(true).parse("{'foo':'BAR'}");

			assertEquals(1, map.size());
			assertEquals("BAR", map.get("foo"));
		});
	}

	@Test
	void testUnquotes() {
		JsonParsers.forEachParser(jsonParser -> {
			Map<String, Object> map = jsonParser.looseMode(true).parse("{foo: BAR , who : me}");

			assertEquals(2, map.size());
			assertEquals("BAR", map.get("foo"));
			assertEquals("me", map.get("who"));

			try {
				jsonParser.looseMode(true).parse("{foo: BAR , who : m\te}");
				fail("error");
			} catch (JsonException ignore) {
			}
		});
	}

}
