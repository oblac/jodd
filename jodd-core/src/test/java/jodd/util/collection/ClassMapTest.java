// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ClassMapTest {

	static Field table;

	@BeforeClass
	public static void start() throws NoSuchFieldException {
		table = ClassMap.class.getDeclaredField("table");
		table.setAccessible(true);
	}


	@Test
	public void testClassMap() throws IllegalAccessException {
		ClassMap<String> classMap = new ClassMap<String>(4);

		classMap.put(Long.class, "longy");
		classMap.put(Integer.class, "inty");
		classMap.put(Float.class, "floaty");
		classMap.put(Double.class, "doubly");
		classMap.put(Byte.class, "bytee");
		classMap.put(Short.class, "shortee");
		classMap.put(Character.class, "charee");
		classMap.put(Collection.class, "aaa");
		classMap.put(Map.class, "aaa");
		classMap.put(TreeMap.class, "aaa");
		classMap.put(HashMap.class, "aaa");
		classMap.put(ArrayList.class, "aaa");

		Object[] t = (Object[]) table.get(classMap);

		assertEquals(12, classMap.size());
		assertEquals(16, t.length);

		classMap.put(List.class, "aaa");
		classMap.put(int[].class, "aaa");
		classMap.put(char[].class, "aaa");

		t = (Object[]) table.get(classMap);
		assertEquals(32, t.length);

		assertEquals("aaa", classMap.get(List.class));
		assertEquals("inty", classMap.get(Integer.class));
		assertEquals("longy", classMap.unsafeGet(Long.class));

		classMap.clear();

		assertEquals(0, classMap.size());
		assertNull(classMap.get(Integer.class));
	}
}