// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd;

import org.junit.Assert;

/**
 * Additional asserts on arrays.
 */
public final class AssertArraysTestHelper {

	public static void assertArrayEquals(Object[][] expected, Object[][] actual) {
		Assert.assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			Assert.assertArrayEquals(expected[i], actual[i]);
		}
	}

	public static void assertArrayEquals(boolean[] expected, boolean[] actual) {
		Assert.assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
	}

	public static void assertArrayEquals(long[][] expected, long[][] actual) {
		Assert.assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			Assert.assertArrayEquals(expected[i], actual[i]);
		}
	}

}
