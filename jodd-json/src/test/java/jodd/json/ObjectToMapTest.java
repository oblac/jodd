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

import jodd.json.fixtures.mock.Address;
import jodd.json.fixtures.mock.Hill;
import jodd.json.fixtures.mock.Person;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ObjectToMapTest {

	@Test
	public void testConvertObjectToMapSimple() {
		Hill hill = new Hill();
		hill.setHeight("173");
		hill.setName("JoddHillWood");

		final Map<String, Object> target = new HashMap<>();

		JsonContext jsonContext = new JsonSerializer().createJsonContext(null);

		BeanSerializer beanSerializer = new BeanSerializer(jsonContext, hill) {
			@Override
			protected void onSerializableProperty(String propertyName, Class propertyType, Object value) {
				target.put(propertyName, value);
			}
		};

		beanSerializer.serialize();
		assertEquals(1, target.size());
		assertEquals("173", target.get("height"));
	}

	@Test
	public void testConvertPersonToMap() {
		Person jodder = new DataCreator().createJodder();

		final Map<String, Object> target = new HashMap<>();

		JsonContext jsonContext = new JsonSerializer().createJsonContext(null);
		BeanSerializer beanSerializer = new BeanSerializer(jsonContext, jodder) {
			@Override
			protected void onSerializableProperty(String propertyName, Class propertyType, Object value) {
				target.put(propertyName, value);
			}
		};

		beanSerializer.serialize();
		assertEquals(6, target.size());

		assertSame(jodder.getBirthdate(), target.get("birthdate"));
		assertSame(jodder.getFirstBaseBallGame(), target.get("firstBaseBallGame"));
		assertSame(jodder.getLastname(), target.get("lastname"));
		assertSame(jodder.getFirstname(), target.get("firstname"));
		assertSame(jodder.getHome(), target.get("home"));
		assertSame(jodder.getWork(), target.get("work"));
	}

	@Test
	public void testConvertPersonToMap2() {
		Person jodder = new DataCreator().createJodder();

		final Map<String, Object> target = new HashMap<>();

		JsonContext jsonContext = new JsonSerializer()
				.include("phones")
				.excludeTypes(Address.class)
				.createJsonContext(null);
		BeanSerializer beanSerializer = new BeanSerializer(jsonContext, jodder) {
			@Override
			protected void onSerializableProperty(String propertyName, Class propertyType, Object value) {
				target.put(propertyName, value);
			}
		};

		beanSerializer.serialize();
		assertEquals(5, target.size());

		assertSame(jodder.getBirthdate(), target.get("birthdate"));
		assertSame(jodder.getFirstBaseBallGame(), target.get("firstBaseBallGame"));
		assertSame(jodder.getLastname(), target.get("lastname"));
		assertSame(jodder.getFirstname(), target.get("firstname"));
		assertSame(jodder.getPhones(), target.get("phones"));
	}

}