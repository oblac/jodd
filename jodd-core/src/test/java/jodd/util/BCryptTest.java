// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BCryptTest {

	@Test
	public void testBCrypt() {
		String hash = BCrypt.hashpw("password", BCrypt.gensalt(7));
		assertTrue(BCrypt.checkpw("password", hash));
	}

	@Test
	public void testBCryptRandom() {
		for (int rounds = 0; rounds< 1000; rounds++) {
			String text = RandomStringUtil.randomAlphaNumeric(10);

			String hash = BCrypt.hashpw(text, BCrypt.gensalt(4));
			assertTrue(BCrypt.checkpw(text, hash));
		}
	}
}