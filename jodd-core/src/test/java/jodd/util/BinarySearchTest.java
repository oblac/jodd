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

package jodd.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinarySearchTest {

	protected List<String> list;
	protected BinarySearch<String> listBinarySearch;

	@BeforeEach
	public void setUp() throws Exception {
		list = new ArrayList<>();
		list.add("aaa");    // 0
		list.add("bbb");
		list.add("ccc");
		list.add("ddd");    // 3
		list.add("ddd");    // 4
		list.add("ddd");    // 5
		list.add("eee");
		list.add("iii");    // 7
		list.add("iii");    // 8
		list.add("sss");

		listBinarySearch = BinarySearch.forList(list);
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testFindRange() {
		assertEquals(3, listBinarySearch.findFirst("ddd"));
		assertEquals(5, listBinarySearch.findLast("ddd", 3, 9));

		assertEquals(7, listBinarySearch.findFirst("iii"));
		assertEquals(8, listBinarySearch.findLast("iii", 7, 9));

		assertEquals(2, listBinarySearch.findFirst("ccc"));
		assertEquals(2, listBinarySearch.findLast("ccc", 2, 9));

	}
}
