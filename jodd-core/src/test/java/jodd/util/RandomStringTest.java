// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RandomStringTest {

	@Test
	public void testRepeatableSequence() {
		RandomString randomString = new RandomString(123);

		String rnd = randomString.randomAlpha(2);

		RandomString randomString2 = new RandomString(123);

		String rnd2 = randomString2.randomAlpha(2);

		assertEquals(rnd, rnd2);
	}

	@Test
	public void testRandomBase64() {
		long iter = 10000;

		RandomString randomString = new RandomString();

		while (iter-->0) {
			String base64 = randomString.randomBase64(10);

			try {
				Base64.decodeToString(base64);
			}
			catch (Exception e) {
				fail(e.getMessage());
			}
		}
	}
}