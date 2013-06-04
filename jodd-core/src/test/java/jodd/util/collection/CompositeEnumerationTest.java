package jodd.util.collection;

import jodd.util.CollectionUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CompositeEnumerationTest {

	@Test
	public void testNextWithOne() {
		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>();
		List<Integer> list = createList(4);
		compositeEnumeration.add(e(list.iterator()));

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

		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>();
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
		List<Integer> list2 = new ArrayList<Integer>();
		List<Integer> list3 = createList(4);
		int count = list1.size() + list2.size() + list3.size();

		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>();
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

		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(it1, it2, it3);

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

		CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(it1, it2, it3);

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
		List<Integer> list = new ArrayList<Integer>(count);
		for (int i = 0; i < count; i++) {
			list.add(Integer.valueOf(i));
		}
		return list;
	}
}