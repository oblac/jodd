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

package jodd.util.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jodd.util.RandomString;

import org.junit.jupiter.api.Test;

public class SortedArrayListTest {

	@Test
	public void testList1() {
		SortedArrayList<String> list = new SortedArrayList<>();

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
			fail("error");
		} catch (UnsupportedOperationException e) {
			// ignore
		}
		try {
			list.set(2, "ddd");
			fail("error");
		} catch (UnsupportedOperationException e) {
			// ignore
		}
		try {
			list.addAll(2, new ArrayList<String>());
			fail("error");
		} catch (UnsupportedOperationException e) {
			// ignore
		}
	}

	@Test
	public void testList2() {
		SortedArrayList<String> list = new SortedArrayList<>();

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

		ArrayList<String> randomList = new ArrayList<>();
		for (int i = 0; i < total; i++) {
			randomList.add(RandomString.getInstance().random(20, 'a', 'z'));
		}

		SortedArrayList<String> sortedList = new SortedArrayList<>(randomList);

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
		SortedArrayList<String> list = new SortedArrayList<>(comparator);
		assertNotNull(list.getComparator());
		list.add("aaa");
		list.add("bbb");
		assertEquals(2, list.size());
		assertEquals("bbb", list.get(0));
		assertEquals("aaa", list.get(1));
		
	}
}
