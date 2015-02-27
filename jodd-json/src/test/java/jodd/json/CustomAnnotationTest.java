// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.json.meta.JSON;
import jodd.json.mock.LocationAlt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomAnnotationTest {

	@Before
	public void setUp() {
		JoddJson.jsonAnnotation = JSON2.class;
	}

	@After
	public void tearDown() {
		JoddJson.jsonAnnotation = JSON.class;
	}

	@Test
	public void testAnnName() {
		LocationAlt location = new LocationAlt();

		location.setLatitude(65);
		location.setLongitude(12);

		String json = new JsonSerializer().serialize(location);

		assertEquals("{\"lat\":65,\"lng\":12}", json);

		LocationAlt jsonLocation = new JsonParser().parse(json, LocationAlt.class);

		assertEquals(location.getLatitude(), jsonLocation.getLatitude());
		assertEquals(location.getLongitude(), jsonLocation.getLongitude());
	}

	@Test
	public void testAnnNameWithClass() {
		LocationAlt location = new LocationAlt();

		location.setLatitude(65);
		location.setLongitude(12);

		String json = new JsonSerializer().setClassMetadataName("class").serialize(location);

		assertEquals("{\"lat\":65,\"lng\":12}", json);

		LocationAlt jsonLocation = new JsonParser().setClassMetadataName("class").parse(json, LocationAlt.class);

		assertEquals(location.getLatitude(), jsonLocation.getLatitude());
		assertEquals(location.getLongitude(), jsonLocation.getLongitude());
	}

}