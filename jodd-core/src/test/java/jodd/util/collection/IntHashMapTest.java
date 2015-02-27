// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntHashMapTest {

	@Test
	public void testIntHashMap() {
		IntHashMap ihm = new IntHashMap();

		assertTrue(ihm.isEmpty());

		for (int i = 0; i < 10000; i++) {
			ihm.put(i, new Integer(i));
		}

		assertEquals(10000, ihm.size());
		assertFalse(ihm.isEmpty());

		for (int i = 0; i < 10000; i++) {
			assertEquals(i, ((Integer) ihm.get(i)).intValue());
		}

		assertTrue(ihm.containsKey(1));
		assertTrue(ihm.containsValue(Integer.valueOf(173)));

		IntHashMap ihm2 = ihm.clone();

		assertEquals(10000, ihm2.size());

		ihm.remove(1);

		assertEquals(9999, ihm.size());
		assertEquals(10000, ihm2.size());

		ihm.clear();

		assertTrue(ihm.isEmpty());

		ihm.put(Integer.valueOf("123"), "Xxx");
		assertEquals("Xxx", ihm.get(123));

		Set<Integer> set = ihm.keySet();
		for (Integer i : set) {
			assertEquals(123, i.intValue());
		}

		for (Map.Entry<Integer, Object> entry : ihm.entrySet()) {
			assertEquals(123, entry.getKey().intValue());
			assertEquals("Xxx", entry.getValue());
		}
	}

}
