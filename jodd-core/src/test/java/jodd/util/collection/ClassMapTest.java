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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ClassMapTest {

	static Field table;

	@BeforeAll
	public static void start() throws NoSuchFieldException {
		table = ClassMap.class.getDeclaredField("table");
		table.setAccessible(true);
	}


	@Test
	public void testClassMap() throws IllegalAccessException {
		ClassMap<String> classMap = new ClassMap<>(4);

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
