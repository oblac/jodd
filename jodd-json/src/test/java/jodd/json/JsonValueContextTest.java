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

import jodd.json.fixtures.mock.Surfer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonValueContextTest {

	@Test
	public void testJsonValueContextBean() {
		Surfer surfer = new Surfer();

		surfer.setName("Igor");
		surfer.setSplit("long wave");

		JsonSerializer jsonSerializer = new JsonSerializer()
				.withSerializer(String.class, new MyTypeJsonSerializer());

		String json = jsonSerializer.serialize(surfer);

		Map<String, Object> map = new JsonParser().parse(json);

		assertEquals("IGOR", map.get("name"));

		assertEquals("{\"id\":0,\"name\":\"IGOR\",\"pipe\":null,\"skill\":null,\"split\":\"long wave\"}", json);
	}

	@Test
	public void testJsonValueContextList() {
		List<String> list = new ArrayList<>();

		list.add("one");
		list.add("two");
		list.add("three");

		JsonSerializer jsonSerializer = new JsonSerializer()
				.withSerializer(String.class, new MyTypeJsonSerializer2());

		String json = jsonSerializer.serialize(list);

		list = new JsonParser().parse(json);

		assertEquals(3, list.size());

		assertEquals("TWO", list.get(1));

		assertEquals("[\"one\",\"TWO\",\"three\"]", json);
	}

	@Test
	public void testJsonValueContextArray() {
		String[] array = new String[] {"one", "two", "three"};

		JsonSerializer jsonSerializer = new JsonSerializer()
				.withSerializer(String.class, new MyTypeJsonSerializer2());

		String json = jsonSerializer.serialize(array);

		List<String> list = new JsonParser().parse(json);

		assertEquals(3, list.size());

		assertEquals("TWO", list.get(1));

		assertEquals("[\"one\",\"TWO\",\"three\"]", json);
	}

	@Test
	public void testJsonValueContextArray2() {
		Object[] array = new Object[] {new Surfer(), "two", "three"};

		JsonSerializer jsonSerializer = new JsonSerializer()
				.withSerializer(String.class, new MyTypeJsonSerializer2());

		String json = jsonSerializer.serialize(array);

		List<String> list = new JsonParser().parse(json);

		assertEquals(3, list.size());

		assertEquals("TWO", list.get(1));
	}

	// ---------------------------------------------------------------- mys

	public static class MyTypeJsonSerializer implements TypeJsonSerializer<String> {

		@Override
		public boolean serialize(JsonContext jsonContext, String value) {
			JsonValueContext jsonValueContext = jsonContext.peekValueContext();

			String propertyName = jsonValueContext.getPropertyName();

			if (propertyName != null && propertyName.equals("name")) {
				value = value.toUpperCase();
			}

			jsonContext.writeString(value);

			return true;
		}
	}

	public static class MyTypeJsonSerializer2 implements TypeJsonSerializer<String> {

		@Override
		public boolean serialize(JsonContext jsonContext, String value) {
			JsonValueContext jsonValueContext = jsonContext.peekValueContext();

			if (jsonValueContext.getIndex() == 1) {
				value = value.toUpperCase();
			}

			jsonContext.writeString(value);

			return true;
		}
	}

}