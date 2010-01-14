// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class CompositeIteratorTest extends TestCase {

	public void testNextWithOne() {
		CompositeIterator compositeIterator = new CompositeIterator();
		List list = createList();
		compositeIterator.add(list.iterator());
		int count = list.size();
		while (compositeIterator.hasNext()) {
			compositeIterator.next();
			count--;
		}
		assertEquals(0, count);
	}

	public void testRemoveWithOne() {
		CompositeIterator compositeIterator = new CompositeIterator();
		List list = createList();
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


	public void testNextWithTwo() {
		CompositeIterator compositeIterator = new CompositeIterator();
		List list = createList();
		int count = list.size();
		compositeIterator.add(list.iterator());
		list = createList();
		count += list.size();
		compositeIterator.add(list.iterator());

		while (compositeIterator.hasNext()) {
			compositeIterator.next();
			count--;
		}
		assertEquals(0, count);
	}

	public void testRemoveWithTwo() {
		List<Integer> list1 = createList();
		List<Integer> list2 = createList();
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

	public void testNextWithThree() {
		List<Integer> list1 = createList();
		List<Integer> list2 = createList();
		List<Integer> list3 = createList();
		int count = list1.size() + list2.size() + list3.size();

		CompositeIterator compositeIterator = new CompositeIterator();
		compositeIterator.add(list1.iterator());
		compositeIterator.add(list2.iterator());
		compositeIterator.add(list3.iterator());

		while (compositeIterator.hasNext()) {
			compositeIterator.next();
			count--;
		}
		assertEquals(0, count);
	}

	public void testRemoveWithThree() {
		List<Integer> list1 = createList();
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = createList();
		int count = list1.size() + list2.size() + list3.size();

		CompositeIterator compositeIterator = new CompositeIterator();
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

	public void testRemoveFail() {
		List<Integer> list1 = createList();
		List<Integer> list2 = createList();

		CompositeIterator compositeIterator = new CompositeIterator();
		compositeIterator.add(list1.iterator());
		compositeIterator.add(list2.iterator());
		try {
			compositeIterator.remove();
			fail();
		} catch (Exception e) {
			// ignore
		}
	}

	// ---------------------------------------------------------------- util

	private List<Integer> createList() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(Integer.valueOf(0));
		list.add(Integer.valueOf(1));
		list.add(Integer.valueOf(2));
		list.add(Integer.valueOf(3));
		return list;
	}

}
