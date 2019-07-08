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
import jodd.json.fixtures.mock.Location;
import jodd.json.fixtures.model.App;
import jodd.json.fixtures.model.MyFolder1;
import jodd.json.fixtures.model.MyFolder2;
import jodd.json.fixtures.model.MyFolder3;
import jodd.json.fixtures.model.MyFolder4;
import jodd.json.fixtures.model.User;
import jodd.json.fixtures.model.UserHolder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnotationTest {

	@Test
	void testAnnName() {
		JsonParsers.forEachParser(jsonParser -> {
			Location location = new Location();

			location.setLatitude(65);
			location.setLongitude(12);

			String json = new JsonSerializer().serialize(location);

			assertEquals("{\"lat\":65,\"lng\":12}", json);

			Location jsonLocation = jsonParser.parse(json, Location.class);

			assertEquals(location.getLatitude(), jsonLocation.getLatitude());
			assertEquals(location.getLongitude(), jsonLocation.getLongitude());
		});
	}

	@Test
	void testAnnNameWithClass() {
		JsonParsers.forEachParser(jsonParser -> {
			Location location = new Location();

			location.setLatitude(65);
			location.setLongitude(12);

			String json = new JsonSerializer().setClassMetadataName("class").serialize(location);

			assertEquals("{\"class\":\"" + Location.class.getName() + "\",\"lat\":65,\"lng\":12}", json);

			Location jsonLocation = jsonParser.setClassMetadataName("class").parse(json, Location.class);

			assertEquals(location.getLatitude(), jsonLocation.getLatitude());
			assertEquals(location.getLongitude(), jsonLocation.getLongitude());
		});
	}

	@Test
	void testAnnIncludeOfCollection() {
		App app = new App();

		String json = new JsonSerializer().serialize(app);

		assertTrue(json.contains("\"apis\":{}"));
		assertTrue(json.contains("\"name\":\"Hello\""));
	}

	@Test
	void testClassInArraySerialize() {
		User user = new User();
		user.setId(123);
		user.setName("joe");

		String json = JsonSerializer.create().serialize(user);

		assertTrue(json.contains("123"));
		assertTrue(json.contains("userId"));
		assertFalse(json.contains("joe"));
		assertFalse(json.contains("name"));

		User[] users = new User[]{user};

		json = JsonSerializer.create().serialize(users);

		assertTrue(json.contains("123"));
		assertTrue(json.contains("userId"));
		assertFalse(json.contains("joe"));
		assertFalse(json.contains("name"));

		List<User> usersList = new ArrayList<>();
		usersList.add(user);

		json = JsonSerializer.create().serialize(usersList);

		assertTrue(json.contains("123"));
		assertTrue(json.contains("userId"));
		assertFalse(json.contains("joe"));
		assertFalse(json.contains("name"));
	}

	@Test
	void testCustomMap() {
		JsonParsers.forEachParser(jsonParser -> {
			String json = "{\"userId\" : 123, \"name\": 456}";

			Map<String, Integer> map = jsonParser.parse(json);
			assertEquals(2, map.size());
			assertEquals(Integer.valueOf(123), map.get("userId"));
			assertEquals(Integer.valueOf(456), map.get("name"));

			Map<String, Long> map2 = JsonParser
				.create()
				.map(JsonParser.VALUES, Long.class)
				.parse(json);

			assertEquals(2, map2.size());
			assertEquals(Long.valueOf(123), map2.get("userId"));
			assertEquals(Long.valueOf(456), map2.get("name"));
		});

		JsonParsers.forEachParser(jsonParser -> {

			String json = "{\"123\" : \"hey\", \"456\": \"man\"}";

			Map<Long, String> map3 = jsonParser
				.map(JsonParser.KEYS, Long.class)
				.parse(json);

			assertEquals(2, map3.size());
			assertEquals("hey", map3.get(Long.valueOf(123)));
			assertEquals("man", map3.get(Long.valueOf(456)));
		});
	}

	@Test
	void testClassInArrayOrMapParse() {
		final String json = "{\"userId\" : 123, \"name\":\"Joe\"}";

		JsonParsers.forEachParser(jsonParser -> {
			User user = jsonParser.parse(json, User.class);

			assertEquals(123, user.getId());
			assertNull(user.getName());
		});

		JsonParsers.forEachParser(jsonParser -> {
			List<User> users = jsonParser.map(JsonParser.VALUES, User.class).parse("[" + json + "]");

			assertEquals(1, users.size());
			User user = users.get(0);
			assertEquals(123, user.getId());
			assertNull(user.getName());
		});

		JsonParsers.forEachParser(jsonParser -> {
			Map<String, Object> map = jsonParser.map(JsonParser.VALUES, User.class).parse("{ \"user\":" + json + "}");

			assertEquals(1, map.size());
			User user = (User) map.get("user");
			assertEquals(123, user.getId());
			assertNull(user.getName());
		});

		JsonParsers.forEachParser(jsonParser -> {
			UserHolder userHolder = jsonParser.parse("{ \"user\":" + json + "}", UserHolder.class);
			assertNotNull(userHolder);
			User user = userHolder.getUser();
			assertEquals(123, user.getId());
			assertNull(user.getName());
		});
	}

	@Test
	void testBeanSettersGetters() {
		String json = "{\"foo.folder\":\"vvvv\"}";

		JsonParsers.forEachParser(jsonParser -> {
			MyFolder1 mf1 = jsonParser.parse(json, MyFolder1.class);
			assertEquals("vvvv", mf1.getFolder());
		});
		JsonParsers.forEachParser(jsonParser -> {
			MyFolder2 mf2 = jsonParser.parse(json, MyFolder2.class);
			assertEquals("vvvv", mf2.get());
		});
		JsonParsers.forEachParser(jsonParser -> {
			MyFolder3 mf3 = jsonParser.parse(json, MyFolder3.class);
			assertEquals("vvvv", mf3.getFolder());
		});
		JsonParsers.forEachParser(jsonParser -> {
			MyFolder4 mf4 = jsonParser.parse(json, MyFolder4.class);
			assertEquals("vvvv", mf4.get());
		});
	}

}
