// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jodd.util.RandomString;

import org.junit.Test;

public class SortedArrayListTest {

	@Test
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
		
		try {
			list.add(2, "ddd");
			fail();
		} catch (UnsupportedOperationException e) {
			// ignore
		}
		try {
			list.set(2, "ddd");
			fail();
		} catch (UnsupportedOperationException e) {
			// ignore
		}
		try {
			list.addAll(2, new ArrayList<String>());
			fail();
		} catch (UnsupportedOperationException e) {
			// ignore
		}
	}

	@Test
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

	@Test
	public void testRandom() {
		int total = 100000;

		ArrayList<String> randomList = new ArrayList<String>();
		for (int i = 0; i < total; i++) {
			randomList.add(RandomString.getInstance().random(20, 'a', 'z'));
		}

		SortedArrayList<String> sortedList = new SortedArrayList<String>(randomList);

		Collections.sort(randomList);

		for (int i = 0; i < total; i++) {
			assertEquals(randomList.get(i), sortedList.get(i));
		}


	}
	
	@Test
	public void testComparator(){
		Comparator<String> comparator = new Comparator<String>() {
			public int compare(String str1, String str2) {
				if (str1 == null && str2 == null) {
					return 0;
				}
				if (str1 == null) {
					return 1;
				}
				if (str2 == null) {
					return -1;
				}
				return str2.compareTo(str1);
			}
		};
		SortedArrayList<String> list = new SortedArrayList<String>(comparator);
		assertNotNull(list.getComparator());
		list.add("aaa");
		list.add("bbb");
		assertEquals(2, list.size());
		assertEquals("bbb", list.get(0));
		assertEquals("aaa", list.get(1));
		
	}
}