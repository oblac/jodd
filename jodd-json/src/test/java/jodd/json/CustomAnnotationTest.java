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