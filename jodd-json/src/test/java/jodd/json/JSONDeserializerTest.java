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
import jodd.json.fixtures.mock.Employee;
import jodd.json.fixtures.mock.Group;
import jodd.json.fixtures.mock.Network;
import jodd.json.fixtures.mock.Pair;
import jodd.json.fixtures.mock.Person;
import jodd.json.fixtures.mock.Phone;
import jodd.json.fixtures.mock.Spiderman;
import jodd.json.fixtures.mock.superhero.Hero;
import jodd.json.fixtures.mock.superhero.SecretIdentity;
import jodd.json.fixtures.mock.superhero.SecretLair;
import jodd.json.fixtures.mock.superhero.SuperPower;
import jodd.json.fixtures.mock.superhero.Villian;
import jodd.json.fixtures.mock.superhero.XRayVision;
import jodd.json.fixtures.model.Account;
import jodd.json.impl.DateJsonSerializer;
import jodd.util.StringUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class JSONDeserializerTest {

	private static final double DELTA = 0.000000001;

	private DataCreator creator;

	@BeforeEach
	void setUp() {
		creator = new DataCreator();
	}

	@AfterEach
	void tearDown() {
		JsonParser.Defaults.classMetadataName = null;
		JsonSerializer.Defaults.classMetadataName = null;
	}

	@Test
	void testDeserializeNoIncludes() {
		JsonParsers.forEachParser(jsonParser -> {
			Person jodder = creator.createJodder();
			String json = new JsonSerializer().serialize(jodder);
			Person jsonJodder = jsonParser.parse(json, Person.class);

			assertNotNull(jsonJodder);

			assertEquals(jodder.getLastname(), jsonJodder.getLastname());
			assertEquals(jodder.getFirstname(), jsonJodder.getFirstname());
			assertEquals(jodder.getBirthdate(), jsonJodder.getBirthdate());

			assertEquals(jodder.getHome().getState(), jsonJodder.getHome().getState());
			assertEquals(jodder.getHome().getStreet(), jsonJodder.getHome().getStreet());
			assertEquals(jodder.getHome().getCity(), jsonJodder.getHome().getCity());

			assertEquals(jodder.getWork().getCity(), jsonJodder.getWork().getCity());

			assertEquals(jsonJodder, jsonJodder.getWork().getPerson());

			assertEquals(0, jsonJodder.getHobbies().size());
		});
	}

	@Test
	void testDeserializeWithPath() {
		JsonParsers.forEachParser(jsonParser -> {

			Person igor = creator.createJodder();
			Map map = new HashMap();
			map.put("person", igor);

			String json = new JsonSerializer().serialize(map);

			map = jsonParser.map("values", Person.class).parse(json);
			Person jsonIgor = (Person) map.get("person");

			assertNotNull(jsonIgor);

			assertEquals(igor.getLastname(), jsonIgor.getLastname());
			assertEquals(igor.getFirstname(), jsonIgor.getFirstname());
			assertEquals(igor.getBirthdate(), jsonIgor.getBirthdate());

			assertEquals(igor.getHome().getState(), jsonIgor.getHome().getState());
			assertEquals(igor.getHome().getStreet(), jsonIgor.getHome().getStreet());
			assertEquals(igor.getHome().getCity(), jsonIgor.getHome().getCity());

			assertEquals(igor.getWork().getCity(), jsonIgor.getWork().getCity());

			assertEquals(jsonIgor, jsonIgor.getWork().getPerson());
		});
	}

	@Test
	void testDeserializeWithIncludes() {
		JsonParsers.forEachParser(jsonParser -> {

			Person igor = creator.createJodder();
			String json = new JsonSerializer().include("phones", "hobbies").serialize(igor);
			Person jsonIgor = jsonParser.parse(json, Person.class);

			assertEquals(2, jsonIgor.getPhones().size());
			assertEquals(0, jsonIgor.getHobbies().size());
		});
	}

	@Test
	void testSubClassDeserialize() {
		JsonParsers.forEachParser(jsonParser -> {

			Employee dilbert = creator.createDilbert();

			String json = new JsonSerializer().include("phones", "hobbies").serialize(dilbert);
			Person jsonDilbert = jsonParser.parse(json, Employee.class);

			assertNotNull(jsonDilbert);
			assertTrue(jsonDilbert instanceof Employee);
			assertEquals(dilbert.getCompany(), ((Employee) jsonDilbert).getCompany());
		});
	}

	@Test
	void testDeserializeInterfaces() {
		JsonParsers.forEachParser(jsonParser -> {

			Hero superman = creator.createSuperman();
			String json = new JsonSerializer().include("powers").setClassMetadataName("class").serialize(superman);
			Hero jsonSuperMan = jsonParser.setClassMetadataName("class").parse(json, Hero.class);

			assertNotNull(jsonSuperMan);
			assertEquals(4, jsonSuperMan.getPowers().size());
			assertHeroHasSuperPowers(jsonSuperMan);
		});
	}

	@Test
	void testDeserializeInterfaces2() {
		JsonParsers.forEachParser(jsonParser -> {

			Hero superman = creator.createSuperman();
			String json = new JsonSerializer().include("powers").withClassMetadata(true).serialize(superman);
			Hero jsonSuperMan = jsonParser.withClassMetadata(true).parse(json, Hero.class);

			assertNotNull(jsonSuperMan);
			assertEquals(4, jsonSuperMan.getPowers().size());
			assertHeroHasSuperPowers(jsonSuperMan);
		});
	}

	@Test
	void testNoClassHints() {
		JsonParsers.forEachParser(jsonParser -> {
			Hero superman = creator.createSuperman();
			String json = new JsonSerializer().exclude("*.class").serialize(superman);

			Hero jsonSuperMan = jsonParser
				.map(Hero.class)
				.map("lair", SecretLair.class)
				.map("secretIdentity", SecretIdentity.class)
				.parse(json);

			assertNotNull(jsonSuperMan);
			assertEquals("Super Man", jsonSuperMan.getName());
			assertNotNull(jsonSuperMan.getIdentity());
			assertEquals("Clark Kent", jsonSuperMan.getIdentity().getName());
			assertNotNull(jsonSuperMan.getLair());
			assertEquals("Fortress of Solitude", jsonSuperMan.getLair().getName());
		});
	}

	@Test
	void testNoHintsButClassesForCollection() {
		JsonParser.Defaults.classMetadataName = "class";
		JsonSerializer.Defaults.classMetadataName = "class";

		JsonParsers.forEachParser(jsonParser -> {

			Hero superman = creator.createSuperman();
			String json = new JsonSerializer()
				.exclude("*.class")
				.include("powers.class")
				.serialize(superman);
			Hero jsonSuperMan = jsonParser.parse(json, Hero.class);
			assertHeroHasSuperPowers(jsonSuperMan);
		});
	}

	@Test
	void testNoClassHintsForCollections() {
		JsonParser.Defaults.classMetadataName = "class";
		JsonSerializer.Defaults.classMetadataName = "class";

		JsonParsers.forEachParser(jsonParser -> {

			Hero superman = creator.createSuperman();
			String json = new JsonSerializer()
				.include("powers")        // redudant
				.include("powers.class")
				.withSerializer("powers.class", new SimpleClassnameTransformer())
				.exclude("*.class")
				.serialize(superman);

			int count = StringUtil.count(json, "***");
			assertEquals(4, count);

			json = StringUtil.remove(json, "***");

			Hero jsonSuperMan = jsonParser
				.map("lair", SecretLair.class)
				.map("secretIdentity", SecretIdentity.class)
				.parse(json, Hero.class);

			assertEquals("Fortress of Solitude", jsonSuperMan.getLair().getName());
			assertHeroHasSuperPowers(jsonSuperMan);
		});
	}

	@Test
	void testListSerialization() {
		JsonParser.Defaults.classMetadataName = "class";
		JsonSerializer.Defaults.classMetadataName = "class";

		Person modesty = creator.createModesty();
		Person igor = creator.createJodder();
		Person pedro = creator.createPedro();
		List<Person> list = new ArrayList<>(3);
		list.add(modesty);
		list.add(igor);
		list.add(pedro);

		JsonParsers.forEachParser(jsonParser -> {
			String json = new JsonSerializer().serialize(list);

			List<Person> people = jsonParser.parse(json);
			assertTrue(people instanceof List);

			json = new JsonSerializer().exclude("*.class").serialize(list);
			people = jsonParser.map("values", Person.class).parse(json);

			assertEquals(3, people.size());
			assertEquals(Person.class, people.get(0).getClass());
		});

		JsonParsers.forEachParser(jsonParser -> {
			String json = new JsonSerializer().exclude("*.class").serialize(list);

			List<Map> peopleMap = jsonParser.parse(json);

			assertEquals(3, peopleMap.size());
			assertTrue(peopleMap.get(0) instanceof Map);
		});
	}

	@Test
	void testGenericTypeDeserialization() {
		JsonParser.Defaults.classMetadataName = "class";
		JsonSerializer.Defaults.classMetadataName = "class";

		JsonParsers.forEachParser(jsonParser -> {

			Pair<Hero, Villian> archenemies = new Pair<>(creator.createSuperman(), creator.createLexLuthor());

			String json = new JsonSerializer()
				.exclude("*.class")
				.serialize(archenemies);

			Pair<Hero, Villian> deserialArchEnemies = jsonParser
				.map("first", Hero.class)
				.map("second", Villian.class)
				.parse(json, Pair.class);

			assertEquals(archenemies.getFirst().getClass(), deserialArchEnemies.getFirst().getClass());
			assertEquals(archenemies.getSecond().getClass(), deserialArchEnemies.getSecond().getClass());

			assertEquals(archenemies.getFirst().getIdentity(), deserialArchEnemies.getFirst().getIdentity());
			assertEquals(archenemies.getFirst().getLair(), deserialArchEnemies.getFirst().getLair());
			assertEquals(archenemies.getFirst().getName(), deserialArchEnemies.getFirst().getName());

			assertEquals(archenemies.getSecond().getName(), deserialArchEnemies.getSecond().getName());
			assertEquals(archenemies.getSecond().getLair(), deserialArchEnemies.getSecond().getLair());
		});
	}

	@Test
	void testGenericTypeDeserialization2() {
		JsonParsers.forEachParser(jsonParser -> {
			Pair<Hero, Villian> archenemies = new Pair<>(creator.createSuperman(), creator.createLexLuthor());

			String json = new JsonSerializer()
				.serialize(archenemies);

			Pair<Hero, Villian> deserialArchEnemies = jsonParser
				.map("first", Hero.class)
				.map("second", Villian.class)
				.parse(json, Pair.class);

			assertEquals(archenemies.getFirst().getClass(), deserialArchEnemies.getFirst().getClass());
			assertEquals(archenemies.getSecond().getClass(), deserialArchEnemies.getSecond().getClass());

			assertEquals(archenemies.getFirst().getIdentity(), deserialArchEnemies.getFirst().getIdentity());
			assertEquals(archenemies.getFirst().getLair(), deserialArchEnemies.getFirst().getLair());
			assertEquals(archenemies.getFirst().getName(), deserialArchEnemies.getFirst().getName());

			assertEquals(archenemies.getSecond().getName(), deserialArchEnemies.getSecond().getName());
			assertEquals(archenemies.getSecond().getLair(), deserialArchEnemies.getSecond().getLair());
		});
	}

	@Test
	void testGeneralMapDeserialization() {
		JsonParser.Defaults.classMetadataName = "class";
		JsonSerializer.Defaults.classMetadataName = "class";

		JsonParsers.forEachParser(jsonParser -> {

			String json = new JsonSerializer().exclude("*.class").serialize(creator.createJodder());
			Map<String, Object> deserialized = jsonParser.parse(json);

			assertEquals("Igor", deserialized.get("firstname"));
			assertEquals("Spasic", deserialized.get("lastname"));
			assertTrue(Map.class.isAssignableFrom(deserialized.get("work").getClass()));
			assertTrue(Map.class.isAssignableFrom(deserialized.get("home").getClass()));
		});
	}

	@Test
	void testGeneralMapDeserialization2() {
		JsonParsers.forEachParser(jsonParser -> {
			String json = new JsonSerializer().serialize(creator.createJodder());
			Map<String, Object> deserialized = jsonParser.parse(json);

			assertEquals("Igor", deserialized.get("firstname"));
			assertEquals("Spasic", deserialized.get("lastname"));
			assertTrue(Map.class.isAssignableFrom(deserialized.get("work").getClass()));
			assertTrue(Map.class.isAssignableFrom(deserialized.get("home").getClass()));
		});
	}

	@Test
	void testListDeserializationNoClass() {
		JsonParser.Defaults.classMetadataName = "class";
		JsonSerializer.Defaults.classMetadataName = "class";

		JsonParsers.forEachParser(jsonParser -> {

			Person modesty = creator.createModesty();
			Person igor = creator.createJodder();
			Person pedro = creator.createPedro();
			List<Person> list = new ArrayList<>(3);
			list.add(modesty);
			list.add(igor);
			list.add(pedro);

			String json = new JsonSerializer().exclude("*.class").serialize(list);
			List<Person> people = jsonParser.map("values", Person.class).parse(json);
			assertEquals(3, list.size());
			assertEquals(modesty.getFirstname(), list.get(0).getFirstname());
			assertEquals(igor.getFirstname(), list.get(1).getFirstname());
			assertEquals(pedro.getFirstname(), list.get(2).getFirstname());
		});
	}

	@Test
	void testListDeserializationNoClass2() {
		JsonParsers.forEachParser(jsonParser -> {
			Person modesty = creator.createModesty();
			Person igor = creator.createJodder();
			Person pedro = creator.createPedro();
			List<Person> list = new ArrayList<>(3);
			list.add(modesty);
			list.add(igor);
			list.add(pedro);

			String json = new JsonSerializer().serialize(list);
			List<Person> people = jsonParser.map("values", Person.class).parse(json);
			assertEquals(3, list.size());
			assertEquals(modesty.getFirstname(), list.get(0).getFirstname());
			assertEquals(igor.getFirstname(), list.get(1).getFirstname());
			assertEquals(pedro.getFirstname(), list.get(2).getFirstname());
		});
	}

	@Test
	void testDateTransforming() {
		JsonParsers.forEachParser(jsonParser -> {
			final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			Person foo = new Person("Foo", "Bar", new Date(), null, null);
			try {
				foo.setBirthdate(df.parse("2009/01/02"));
			} catch (ParseException pe) {
				fail(pe);
			}


			String json = new JsonSerializer().withSerializer("birthdate", new DateJsonSerializer() {
				@Override
				public boolean serialize(JsonContext jsonContext, Date date) {
					jsonContext.writeString(df.format(date));
					return true;
				}
			}).serialize(foo);

			Person newUser = jsonParser
				.withValueConverter("birthdate", (ValueConverter<String, Date>) data -> {
					try {
						return df.parse(data);
					} catch (ParseException pe) {
						throw new JsonException(pe);
					}
				})
				.parse(json, Person.class);

			assertEquals(foo.getBirthdate(), newUser.getBirthdate());
			assertEquals("2009/01/02", df.format(newUser.getBirthdate()));
		});
	}

	@Test
	void testMapWithEmbeddedObject() {
		JsonParsers.forEachParser(jsonParser -> {
			Map<String, Network> networks = jsonParser
				.setClassMetadataName("class")
				.parse("{\"1\": {\"class\":\"" + Network.class.getName() + "\", \"name\": \"Jodd\"} }");

			assertNotNull(networks);
			assertEquals(1, networks.size());
			assertTrue(networks.containsKey("1"));
			assertNotNull(networks.get("1"));
			assertEquals(Network.class, networks.get("1").getClass());
			assertEquals("Jodd", networks.get("1").getName());
		});
	}

	@Test
	void testMapWithEmbeddedObject2() {
		JsonParsers.forEachParser(jsonParser -> {
			Map<String, Pair<Phone, Network>> complex = jsonParser
				.map("values", Pair.class)
				.map("values.first", Phone.class)
				.map("values.second", Network.class)
				.parse("{\"1\": { \"first\": { \"areaCode\": \"404\" }, \"second\": {\"name\": \"Jodd\"} } }");
			assertNotNull(complex);
			assertEquals(1, complex.size());
			assertTrue(complex.containsKey("1"));
			assertNotNull(complex.get("1"));
			assertEquals(Pair.class, complex.get("1").getClass());
			assertEquals(Phone.class, complex.get("1").getFirst().getClass());
			assertEquals(Network.class, complex.get("1").getSecond().getClass());
			assertEquals("404", complex.get("1").getFirst().getAreaCode());
			assertEquals("Jodd", complex.get("1").getSecond().getName());
		});
	}

	@Test
	void testListWithEmbeddedObject() {
		JsonParsers.forEachParser(jsonParser -> {
			List<Network> networks = jsonParser
				.setClassMetadataName("class")
				.parse("[" +
					"	{\"class\":\"" + Network.class.getName() + "\", \"name\": \"Jodd\"}," +
					"	{\"class\":\"" + Network.class.getName() + "\", \"name\": \"Mojo\"}" +
					"]");

			assertNotNull(networks);
			assertEquals(2, networks.size());
			Network network = networks.get(0);
			assertEquals("Jodd", network.getName());
			network = networks.get(1);
			assertEquals("Mojo", network.getName());
		});
	}


	@Test
	void testArrayType() {
		JsonParsers.forEachParser(jsonParser -> {
			Person igor = creator.createJodder();
			Person modesty = creator.createModesty();

			Group group = new Group("brothers", igor, modesty);
			String json = new JsonSerializer().include("people").exclude("*.class").serialize(group);
			Group bro = jsonParser.map(Group.class).parse(json);

			assertNotNull(bro);
			assertEquals("brothers", bro.getGroupName());
			assertEquals(2, bro.getPeople().length);
			assertEquals("Igor", bro.getPeople()[0].getFirstname());
			assertEquals("Modesty", bro.getPeople()[1].getFirstname());
		});
	}

	@Test
	void testEmptyArray() {
		JsonParsers.forEachParser(jsonParser -> {
			Group group = jsonParser.parse("{\"people\": [], \"groupName\": \"Nobody\" }", Group.class);
			assertEquals("Nobody", group.getGroupName());
			assertEquals(0, group.getPeople().length);
		});
	}


	@Test
	void testNullDeserialization() {
		JsonParsers.forEachParser(jsonParser -> {
			String input = "{\"property\": null, \"property2\":5, \"property3\":\"abc\"}";

			JsonParser deserializer = jsonParser;
			deserializer.map(null, HashMap.class);
			Map<String, Object> result = deserializer.parse(input);

			assertNotNull(result);
			// fails on this line, because the first property is not deserialized
			assertEquals(3, result.size());
			assertTrue(result.containsKey("property"));
			assertNull(result.get("property"), "the value should be null");
		});
	}

	@Test
	void testPrimitives() {
		JsonParsers.forEachParser(jsonParser -> {
			List<Date> dates = new ArrayList<>();
			dates.add(new Date());
			dates.add(new Date(1970, 1, 12));
			dates.add(new Date(1986, 3, 21));

			String json = new JsonSerializer().serialize(dates);
			List<Date> jsonDates = jsonParser
				.map(null, ArrayList.class)
				.map("values", Date.class)
				.parse(json);

			assertEquals(jsonDates.size(), dates.size());
			assertEquals(Date.class, jsonDates.get(0).getClass());
		});

		JsonParsers.forEachParser(jsonParser -> {
			List<? extends Number> numbers = Arrays.asList(1, 0.5, 100.4f, (short) 5);
			String json = new JsonSerializer().serialize(numbers);
			List<Number> jsonNumbers = jsonParser.parse(json);

			assertEquals(numbers.size(), jsonNumbers.size());
			for (int i = 0; i < numbers.size(); i++) {
				assertEquals(numbers.get(i).floatValue(), jsonNumbers.get(i).floatValue(), DELTA);
			}

			assertEquals(numbers.size(), jsonNumbers.size());
		});

		JsonParsers.forEachParser(jsonParser -> {
			List<Boolean> bools = Arrays.asList(true, false, true, false, false);
			String json = new JsonSerializer().serialize(bools);
			List<Boolean> jsonBools = jsonParser.parse(json);

			assertEquals(bools.size(), jsonBools.size());
			for (int i = 0; i < bools.size(); i++) {
				assertEquals(bools.get(i), jsonBools.get(i));
			}
		});
	}

	@Test
	void testArray() {
		JsonParsers.forEachParser(jsonParser -> {
			Person[] p = new Person[3];
			p[0] = creator.createJodder();
			p[1] = creator.createDilbert();
			p[2] = creator.createModesty();

			String json = new JsonSerializer().serialize(p);

			Person[] jsonP = jsonParser.parse(json, Person[].class);

			assertEquals(3, jsonP.length);
			assertEquals("Igor", jsonP[0].getFirstname());
			assertEquals("Dilbert", jsonP[1].getFirstname());
			assertEquals("Modesty", jsonP[2].getFirstname());
		});
	}

	@Test
	void testArray_boolean() {
		JsonParsers.forEachParser(jsonParser -> {
			final boolean[] input = new boolean[]{true, false, true};
			final boolean[] expected_bools = input;
			final String expected_json = "[true,false,true]";

			final String actual_json = new JsonSerializer().serialize(input);
			final boolean[] actual_bools = jsonParser.parse(actual_json, boolean[].class);

			// asserts
			assertNotNull(actual_json);
			assertNotNull(actual_bools);
			assertEquals(expected_json, actual_json);
			assertArrayEquals(expected_bools, actual_bools);
		});
	}

	@Test
	void testDeserializationIntoPublicFields() {
		JsonParser.Defaults.classMetadataName = "class";
		JsonSerializer.Defaults.classMetadataName = "class";

		JsonParsers.forEachParser(jsonParser -> {
			Spiderman spiderman = new Spiderman();
			spiderman.spideySense = false;
			spiderman.superpower = "Creates Many Webs and Super Tough";

			String json = new JsonSerializer().serialize(spiderman);
			Spiderman jsonSpiderman = jsonParser.parse(json);

			assertEquals(spiderman.spideySense, jsonSpiderman.spideySense);
			assertEquals(spiderman.superpower, jsonSpiderman.superpower);
		});
	}

	@Test
	void testAutoTypeConvertToNumerical() {
		JsonParsers.forEachParser(jsonParser -> {
			Account account = jsonParser
				.parse("{\"id\": \"5\", \"accountNumber\": \"1234567-123\"}", Account.class);
			assertEquals(new Integer(5), account.getId());

			XRayVision xray = jsonParser.parse("{ \"power\": \"2.3\" }", XRayVision.class);
			assertEquals(2.3f, xray.getPower(), DELTA);
		});
	}

	@Test
	void testDeserializeURL() {
		JsonParsers.forEachParser(jsonParser -> {
			String json = "{\n" +
				"  \"oslc_cm:next\": \"http:\\/\\/localhost:9080\\/results\\/3\",\n" +
				"  \"oslc_cm:previous\": \"http:\\/\\/localhost:9080\\/results\\/1\", \n" +
				"  \"oslc_cm:totalCount\" : 27,\n" +
				"  \"oslc_cm:results\": [\n" +
				"    {\n" +
				"      \"rdf:resource\": \"http:\\/\\/localhost:9080\\/records\\/1234\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"rdf:resource\": \"http:\\/\\/localhost:9080\\/records\\/1235\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"rdf:resource\": \"http:\\/\\/localhost:9080\\/records\\/1236\"\n" +
				"    }   \n" +
				"  ]\n" +
				"}";
			Map<String, Object> page2 = jsonParser.parse(json);
			assertEquals("http://localhost:9080/results/3", page2.get("oslc_cm:next"));
			assertEquals(3, ((List) page2.get("oslc_cm:results")).size());
		});
	}

	@Test
	void testPoint() {
		JsonParser.Defaults.classMetadataName = "__class";
		JsonSerializer.Defaults.classMetadataName = "__class";

		JsonParsers.forEachParser(jsonParser -> {
			String json = new JsonSerializer().serialize(new Point2D.Float(1.0f, 2.0f));
			Point2D.Float point = jsonParser.parse(json);
			assertEquals(1.0f, point.x, DELTA);
			assertEquals(2.0f, point.y, DELTA);
		});
	}

	@Test
	void testPointWithException() {
		JsonParser.Defaults.classMetadataName = "__class";
		JsonSerializer.Defaults.classMetadataName = "__class";

		JsonParsers.forEachParser(jsonParser -> {
			jsonParser.allowClass("notAllowed");
			final String json = new JsonSerializer().serialize(new Point2D.Float(1.0f, 2.0f));
			assertThrows(JsonException.class, () -> {
				jsonParser.parse(json);
			});
			jsonParser.allowAllClasses();
		});
	}

	@Test
	void testPointWithoutExceptionWhitelisted() {
		JsonParser.Defaults.classMetadataName = "__class";
		JsonSerializer.Defaults.classMetadataName = "__class";

		JsonParsers.forEachParser(jsonParser -> {
			jsonParser.allowClass("*.Point?D*");
			String json = new JsonSerializer().serialize(new Point2D.Float(1.0f, 2.0f));
			Point2D.Float point = jsonParser.parse(json);
			assertEquals(1.0f, point.x, DELTA);
			assertEquals(2.0f, point.y, DELTA);
			jsonParser.allowAllClasses();
		});
	}


	@Test
	void testUnixEpoch() {
		JsonParsers.forEachParser(jsonParser -> {
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("GMT"));

			cal.set(Calendar.YEAR, 1970);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.AM_PM, Calendar.AM);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			Person hank = new Person("Hank", "Paulsen", cal.getTime(), null, null);

			String json = new JsonSerializer().serialize(hank);
			Person deHank = jsonParser.parse(json, Person.class);

			assertEquals(hank.getFirstname(), deHank.getFirstname());
			assertEquals(hank.getLastname(), deHank.getLastname());
			assertEquals(hank.getBirthdate(), deHank.getBirthdate());
		});
	}

	public static class SimpleClassnameTransformer implements TypeJsonSerializer {
		@Override
		public boolean serialize(JsonContext jsonContext, Object value) {
			String name = value.toString() + "***";
			jsonContext.writeString(name);
			return true;
		}
	}

	private void assertHeroHasSuperPowers(Hero hero) {
		for (int i = 0; i < hero.getPowers().size(); i++) {
			assertTrue(hero.getPowers().get(i) instanceof SuperPower);
		}
	}

}
