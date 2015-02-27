// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.json.mock.Address;
import jodd.json.mock.Hill;
import jodd.json.mock.Person;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ObjectToMapTest {

	@Test
	public void testConvertObjectToMapSimple() {
		Hill hill = new Hill();
		hill.setHeight("173");
		hill.setName("JoddHillWood");

		final Map<String, Object> target = new HashMap<String, Object>();

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

		final Map<String, Object> target = new HashMap<String, Object>();

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

		final Map<String, Object> target = new HashMap<String, Object>();

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