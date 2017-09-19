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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class CompositeIteratorTest {

	@Test
	public void testNextWithOne() {
		CompositeIterator<Integer> compositeIterator = new CompositeIterator<>();
		
		try {
			compositeIterator.next();
			fail("error");
		} catch (NoSuchElementException e) {
			// ignore
		}
		
		List<Integer> list = createList(4);
		Iterator<Integer> iterator = list.iterator();
		compositeIterator.add(iterator);
		
		try {
			compositeIterator.add(iterator);
			fail("error");
		} catch (IllegalArgumentException iaex) {
			// ignore
		}

		int count = list.size();
		StringBuilder sb = new StringBuilder();
		while (compositeIterator.hasNext()) {
			sb.append(compositeIterator.next());
			count--;
		}
		assertEquals(0, count);
		assertEquals("0123", sb.toString());
	}

	@Test
	public void testRemoveWithOne() {
		CompositeIterator compositeIterator = new CompositeIterator();
		List list = createList(4);
		compositeIterator.add(list.iterator());
		int count = list.size();
		while (compositeIterator.hasNext()) {
			compositeIterator.next();
			compositeIterator.remove();
			count--;
		}
		assertEquals(0, count);
		assertEquals(0, list.size());
	}


	@Test
	public void testNextWithTwo() {
		CompositeIterator compositeIterator = new CompositeIterator();
		List list = createList(4);
		int count = list.size();
		compositeIterator.add(list.iterator());
		list = createList(4);
		count += list.size();
		compositeIterator.add(list.iterator());

		StringBuilder sb = new StringBuilder();
		while (compositeIterator.hasNext()) {
			sb.append(compositeIterator.next());
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

		CompositeIterator compositeIterator = new CompositeIterator();
		compositeIterator.add(list1.iterator());
		compositeIterator.add(list2.iterator());

		while (compositeIterator.hasNext()) {
			compositeIterator.next();
			compositeIterator.remove();
			count--;
		}

		assertEquals(0, count);
		assertEquals(0, list1.size());
		assertEquals(0, list2.size());
	}

	@Test
	public void testNextWithThree() {
		List<Integer> list1 = createList(4);
		List<Integer> list2 = createList(4);
		List<Integer> list3 = createList(4);
		int count = list1.size() + list2.size() + list3.size();

		CompositeIterator<Integer> compositeIterator = new CompositeIterator<>();
		compositeIterator.add(list1.iterator());
		compositeIterator.add(list2.iterator());
		compositeIterator.add(list3.iterator());

		while (compositeIterator.hasNext()) {
			compositeIterator.next();
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

		CompositeIterator<Integer> compositeIterator = new CompositeIterator<>();
		compositeIterator.add(list1.iterator());
		compositeIterator.add(list2.iterator());
		compositeIterator.add(list3.iterator());

		while (compositeIterator.hasNext()) {
			compositeIterator.next();
			compositeIterator.remove();
			count--;
		}
		assertEquals(0, count);
		assertEquals(0, list1.size());
		assertEquals(0, list2.size());
		assertEquals(0, list3.size());
	}

	@Test
	public void testRemoveFail() {
		List<Integer> list1 = createList(4);
		List<Integer> list2 = createList(4);

		CompositeIterator<Integer> compositeIterator = new CompositeIterator<>();
		compositeIterator.add(list1.iterator());
		compositeIterator.add(list2.iterator());
		try {
			compositeIterator.remove();
			fail("error");
		} catch (Exception e) {
			// ignore
		}
	}

	@Test
	public void testPartialIterationWithThree1() {
		List<Integer> list1 = createList(4);
		Iterator<Integer> it1 = list1.iterator();
		List<Integer> list2 = createList(3);
		Iterator<Integer> it2 = list2.iterator();
		List<Integer> list3 = createList(2);
		Iterator<Integer> it3 = list3.iterator();

		it1.next();
		it2.next();
		it3.next();

		CompositeIterator<Integer> compositeIterator = new CompositeIterator<>(it1, it2, it3);

		int count = 0;
		StringBuilder sb = new StringBuilder();
		while (compositeIterator.hasNext()) {
			Integer next = compositeIterator.next();
			sb.append(next);
			count++;
		}

		assertEquals(6, count);
		assertEquals("123121", sb.toString());
	}

	@Test
	public void testPartialIterationWithThree2() {
		List<Integer> list1 = createList(4);
		Iterator<Integer> it1 = list1.iterator();
		List<Integer> list2 = createList(3);
		Iterator<Integer> it2 = list2.iterator();
		List<Integer> list3 = createList(2);
		Iterator<Integer> it3 = list3.iterator();

		it1.next();
		it2.next(); it2.next(); it2.next();
		it3.next();

		CompositeIterator<Integer> compositeIterator = new CompositeIterator<>(it1, it2, it3);

		int count = 0;
		StringBuilder sb = new StringBuilder();
		while (compositeIterator.hasNext()) {
			Integer next = compositeIterator.next();
			sb.append(next);
			count++;
		}

		assertEquals(4, count);
		assertEquals("1231", sb.toString());
	}

	// ---------------------------------------------------------------- util

	private List<Integer> createList(int count) {
		List<Integer> list = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			list.add(Integer.valueOf(i));
		}
		return list;
	}

}
