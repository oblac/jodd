// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchTest extends TestCase {

	protected List<String> list;
	protected BinarySearch<String> listBinarySearch;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		list = new ArrayList<String>();
		list.add("aaa");	// 0
		list.add("bbb");
		list.add("ccc");
		list.add("ddd");	// 3
		list.add("ddd");	// 4
		list.add("ddd");	// 5
		list.add("eee");
		list.add("iii");	// 7
		list.add("iii");	// 8
		list.add("sss");

		listBinarySearch = BinarySearch.forList(list);
	}

	public void testFind() {
		assertEquals(0, listBinarySearch.find("aaa"));
		assertEquals(1, listBinarySearch.find("bbb"));
		assertEquals(2, listBinarySearch.find("ccc"));
		assertEquals(6, listBinarySearch.find("eee"));
		assertEquals(9, listBinarySearch.find("sss"));
		assertEquals(6, listBinarySearch.find("eee", 4, 6));

		assertTrue(listBinarySearch.find("aaaaa") < 0);
		assertTrue(listBinarySearch.find("aaa", 1, 4) < 0);
		assertTrue(listBinarySearch.find("eee", 4, 5) < 0);

		int ndx = listBinarySearch.find("ddd");
		assertTrue(ndx == 3 || ndx == 4 || ndx == 5);

		ndx = listBinarySearch.find("iii");
		assertTrue(ndx == 7 || ndx == 8);
	}

	public void testFindFirst() {
		assertEquals(0, listBinarySearch.findFirst("aaa"));
		assertEquals(1, listBinarySearch.findFirst("bbb"));
		assertEquals(2, listBinarySearch.findFirst("ccc"));
		assertEquals(6, listBinarySearch.findFirst("eee"));
		assertEquals(9, listBinarySearch.findFirst("sss"));
		assertEquals(6, listBinarySearch.findFirst("eee", 4, 6));

		assertTrue(listBinarySearch.findFirst("aaaaa") < 0);
		assertTrue(listBinarySearch.findFirst("aa") < 0);
		assertTrue(listBinarySearch.findFirst("aaa", 1, 4) < 0);
		assertTrue(listBinarySearch.findFirst("eee", 4, 5) < 0);

		assertEquals(3, listBinarySearch.findFirst("ddd"));
		assertEquals(7, listBinarySearch.findFirst("iii"));
	}

	public void testFindLast() {
		assertEquals(0, listBinarySearch.findLast("aaa"));
		assertEquals(6, listBinarySearch.findLast("eee"));
		assertEquals(9, listBinarySearch.findLast("sss"));
		assertEquals(6, listBinarySearch.findLast("eee", 4, 6));

		assertTrue(listBinarySearch.findLast("aaaaa") < 0);
		assertTrue(listBinarySearch.findLast("aaa", 1, 4) < 0);
		assertTrue(listBinarySearch.findLast("eee", 4, 5) < 0);

		assertEquals(5, listBinarySearch.findLast("ddd"));
		assertEquals(8, listBinarySearch.findLast("iii"));
	}

	public void testFindRange() {
		assertEquals(3, listBinarySearch.findFirst("ddd"));
		assertEquals(5, listBinarySearch.findLast("ddd", 3));

		assertEquals(7, listBinarySearch.findFirst("iii"));
		assertEquals(8, listBinarySearch.findLast("iii"), 7);

		assertEquals(2, listBinarySearch.findFirst("ccc"));
		assertEquals(2, listBinarySearch.findLast("ccc", 2));

	}
}
