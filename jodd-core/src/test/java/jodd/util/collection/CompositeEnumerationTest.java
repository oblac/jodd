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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import jodd.util.CollectionUtil;

import org.junit.jupiter.api.Test;

public class CompositeEnumerationTest {

	@Test
	public void testNextWithOne() {
		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<>();
		
		try {
			compositeEnumeration.nextElement();
			fail("error");
		} catch (NoSuchElementException e) {
			// ignore
		}
		
		List<Integer> list = createList(4);
		Enumeration<Integer> e = e(list.iterator());
		compositeEnumeration.add(e);
		
		try {
			compositeEnumeration.add(e);
			fail("error");
		} catch (IllegalArgumentException iaex) {
			// ignore
		}

		int count = list.size();
		StringBuilder sb = new StringBuilder();
		while (compositeEnumeration.hasMoreElements()) {
			sb.append(compositeEnumeration.nextElement());
			count--;
		}
		assertEquals(0, count);
		assertEquals("0123", sb.toString());
	}

	@Test
	public void testRemoveWithOne() {
		CompositeEnumeration compositeEnumeration = new CompositeEnumeration();
		List list = createList(4);
		compositeEnumeration.add(e(list.iterator()));
		int count = list.size();
		while (compositeEnumeration.hasMoreElements()) {
			compositeEnumeration.nextElement();
			count--;
		}
		assertEquals(0, count);
	}


	@Test
	public void testNextWithTwo() {
		CompositeEnumeration compositeEnumeration = new CompositeEnumeration();
		List list = createList(4);
		int count = list.size();
		compositeEnumeration.add(e(list.iterator()));
		list = createList(4);
		count += list.size();
		compositeEnumeration.add(e(list.iterator()));

		StringBuilder sb = new StringBuilder();
		while (compositeEnumeration.hasMoreElements()) {
			sb.append(compositeEnumeration.nextElement());
			count--;
		}
		assertEquals(0, count);
		assertEquals("01230123", sb.toString());
	}

	@Test
	public void testRemoveWithTwo() {
		List<Integer> list1 = createList(4);
		List<Integer> list2 = createList(4);
		int count = list1.size() + list2.size();

		CompositeEnumeration compositeEnumeration = new CompositeEnumeration();
		compositeEnumeration.add(e(list1.iterator()));
		compositeEnumeration.add(e(list2.iterator()));

		while (compositeEnumeration.hasMoreElements()) {
			compositeEnumeration.nextElement();
			count--;
		}

		assertEquals(0, count);
	}

	@Test
	public void testNextWithThree() {
		List<Integer> list1 = createList(4);
		List<Integer> list2 = createList(4);
		List<Integer> list3 = createList(4);
		int count = list1.size() + list2.size() + list3.size();

		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<>();
		compositeEnumeration.add(e(list1.iterator()));
		compositeEnumeration.add(e(list2.iterator()));
		compositeEnumeration.add(e(list3.iterator()));

		while (compositeEnumeration.hasMoreElements()) {
			compositeEnumeration.nextElement();
			count--;
		}
		assertEquals(0, count);
	}

	@Test
	public void testRemoveWithThree() {
		List<Integer> list1 = createList(4);
		List<Integer> list2 = new ArrayList<>();
		List<Integer> list3 = createList(4);
		int count = list1.size() + list2.size() + list3.size();

		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<>();
		compositeEnumeration.add(e(list1.iterator()));
		compositeEnumeration.add(e(list2.iterator()));
		compositeEnumeration.add(e(list3.iterator()));

		while (compositeEnumeration.hasMoreElements()) {
			compositeEnumeration.nextElement();
			count--;
		}
		assertEquals(0, count);
	}

	@Test
	public void testPartialIterationWithThree1() {
		List<Integer> list1 = createList(4);
		Enumeration<Integer> it1 = e(list1.iterator());
		List<Integer> list2 = createList(3);
		Enumeration<Integer> it2 = e(list2.iterator());
		List<Integer> list3 = createList(2);
		Enumeration<Integer> it3 = e(list3.iterator());

		it1.nextElement();
		it2.nextElement();
		it3.nextElement();

		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<>(it1, it2, it3);

		int count = 0;
		StringBuilder sb = new StringBuilder();
		while (compositeEnumeration.hasMoreElements()) {
			Integer next = compositeEnumeration.nextElement();
			sb.append(next);
			count++;
		}

		assertEquals(6, count);
		assertEquals("123121", sb.toString());
	}

	@Test
	public void testPartialIterationWithThree2() {
		List<Integer> list1 = createList(4);
		Enumeration<Integer> it1 = e(list1.iterator());
		List<Integer> list2 = createList(3);
		Enumeration<Integer> it2 = e(list2.iterator());
		List<Integer> list3 = createList(2);
		Enumeration<Integer> it3 = e(list3.iterator());

		it1.nextElement();
		it2.nextElement(); it2.nextElement(); it2.nextElement();
		it3.nextElement();

		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<>(it1, it2, it3);

		int count = 0;
		StringBuilder sb = new StringBuilder();
		while (compositeEnumeration.hasMoreElements()) {
			Integer next = compositeEnumeration.nextElement();
			sb.append(next);
			count++;
		}

		assertEquals(4, count);
		assertEquals("1231", sb.toString());
	}

	// ---------------------------------------------------------------- util

	private Enumeration<Integer> e(Iterator<Integer> iterator) {
		return CollectionUtil.asEnumeration(iterator);
	}

	private List<Integer> createList(int count) {
		List<Integer> list = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			list.add(Integer.valueOf(i));
		}
		return list;
	}
}
