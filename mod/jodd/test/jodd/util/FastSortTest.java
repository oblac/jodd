// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class FastSortTest extends TestCase {

	static Random rnd = new Random();

	public void testSort1() {
		String[] strings = new String[1024*100];

		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			strings[i] = RandomStringUtil.randomAlphaNumeric(10 + rnd.nextInt(100));
		}

		String[] expected = strings.clone();

		FastSort.sort(strings);
		Arrays.sort(expected);

		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			assertEquals(expected[i], strings[i]);
		}
	}

	public void testSort2() {
		String[] strings = new String[1024*100];

		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			strings[i] = RandomStringUtil.randomAlphaNumeric(10 + rnd.nextInt(100));
		}

		String[] expected = strings.clone();

		FastSort.sort(strings, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		Arrays.sort(expected, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			assertEquals(expected[i], strings[i]);
		}
	}
}
