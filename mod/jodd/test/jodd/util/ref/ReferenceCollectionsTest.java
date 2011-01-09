// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.ref;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ReferenceCollectionsTest extends TestCase {

	@SuppressWarnings({"unchecked"})
	public void testReferenceMap() {
		HashMap map = new HashMap();
		ReferenceMap rm = new ReferenceMap(ReferenceType.SOFT, ReferenceType.WEAK);

		map.put("xxx", "123");
		rm.put("xxx", "123");
		assertEquals(map.get("xxx"), rm.get("xxx"));
		assertEquals(map.size(), rm.size());
		assertEquals(map.containsKey("xxx"), rm.containsKey("xxx"));
		assertEquals(map.containsKey("---"), rm.containsKey("---"));
		assertEquals(map.containsValue("123"), rm.containsValue("123"));
		assertEquals(map.containsValue("---"), rm.containsValue("---"));

		Set<Map.Entry> es = rm.entrySet();
		assertEquals(map.size(), es.size());
		Iterator it = es.iterator();
		assertTrue(it.hasNext());
		Map.Entry e = (Map.Entry) it.next();
		assertEquals("xxx", e.getKey());
		assertEquals("123", e.getValue());
		e.setValue("456");
		assertEquals("456", e.getValue());
		assertFalse(it.hasNext());
		Object[] arr = es.toArray();
		assertEquals(map.size(), arr.length);
		assertEquals("xxx", ((Map.Entry)arr[0]).getKey());
		assertEquals("456", ((Map.Entry)arr[0]).getValue());



		Set keys = rm.keySet();
		assertEquals(map.size(), keys.size());
		assertTrue(keys.contains("xxx"));
		assertFalse(keys.contains("---"));
		it = keys.iterator();
		assertTrue(it.hasNext());
		assertEquals("xxx", it.next());
		assertFalse(it.hasNext());

		Collection values = rm.values();
		assertEquals(values.size(), keys.size());
		assertTrue(values.contains("456"));
		assertFalse(values.contains("---"));
		it = values.iterator();
		assertTrue(it.hasNext());
		assertEquals("456", it.next());
		assertFalse(it.hasNext());
		values.remove("456");
		assertTrue(values.isEmpty());

		assertTrue(rm.isEmpty());
	}

	@SuppressWarnings({"unchecked"})
	public void testReferenceSet() {
		HashSet set = new HashSet();
		ReferenceSet rs = new ReferenceSet(ReferenceType.SOFT);

		set.add("xxx");
		rs.add("xxx");

		assertEquals(set.size(), rs.size());
		assertTrue(rs.contains("xxx"));

		Iterator i = rs.iterator();
		assertTrue(i.hasNext());
		assertEquals("xxx", i.next());
		assertFalse(i.hasNext());

		assertTrue(rs.remove("xxx"));
		assertTrue(rs.isEmpty());
	}

}
