// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import jodd.util.RandomStringUtil;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;

public class SortedArrayListTest extends TestCase {

	public void testList1() {
		SortedArrayList<String> list = new SortedArrayList<String>();

		list.add("aaa");
		list.add("bbb");

		assertEquals(2, list.size());
		assertEquals("aaa", list.get(0));
		assertEquals("bbb", list.get(1));

		list.add("ccc");
		assertEquals(3, list.size());
		assertEquals("ccc", list.get(2));

		list.add("cc");
		assertEquals(4, list.size());
		assertEquals("cc", list.get(2));
	}

	public void testList2() {
		SortedArrayList<String> list = new SortedArrayList<String>();

		list.add("bbb");
		list.add("aaa");

		assertEquals(2, list.size());
		assertEquals("aaa", list.get(0));
		assertEquals("bbb", list.get(1));

		list.add("aa");
		assertEquals(3, list.size());
		assertEquals("aa", list.get(0));

		list.add("a");
		assertEquals(4, list.size());
		assertEquals("a", list.get(0));

		assertEquals(1, list.findInsertionPoint("a"));
	}

	public void testRandom() {
		int total = 100000;

		ArrayList<String> randomList = new ArrayList<String>();
		for (int i = 0; i < total; i++) {
			randomList.add(RandomStringUtil.random(20, 'a', 'z'));
		}

		SortedArrayList<String> sortedList = new SortedArrayList<String>(randomList);

		Collections.sort(randomList);

		for (int i = 0; i < total; i++) {
			assertEquals(randomList.get(i), sortedList.get(i));
		}


	}
}
