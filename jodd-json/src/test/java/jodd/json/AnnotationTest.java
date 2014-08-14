// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.json.mock.Location;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnnotationTest {

	@Test
	public void testAnnName() {
		Location location = new Location();

		location.setLatitude(65);
		location.setLongitude(12);

		String json = new JsonSerializer().serialize(location);

		assertEquals("{\"lat\":65,\"lng\":12}", json);

		Location jsonLocation = new JsonParser().parse(json, Location.class);

		assertEquals(location.getLatitude(), jsonLocation.getLatitude());
		assertEquals(location.getLongitude(), jsonLocation.getLongitude());
	}

	@Test
	public void testAnnNameWithClass() {
		Location location = new Location();

		location.setLatitude(65);
		location.setLongitude(12);

		String json = new JsonSerializer().setClassMetadataName("class").serialize(location);

		assertEquals("{\"class\":\"jodd.json.mock.Location\",\"lat\":65,\"lng\":12}", json);

		Location jsonLocation = new JsonParser().setClassMetadataName("class").parse(json, Location.class);

		assertEquals(location.getLatitude(), jsonLocation.getLatitude());
		assertEquals(location.getLongitude(), jsonLocation.getLongitude());
	}

}