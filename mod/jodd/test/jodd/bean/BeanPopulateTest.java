// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanPopulateTest extends TestCase {
	
	public static class Bean {
		Foo foo;
	}
	
	public static class Foo {
		String one;
		int two;
		long[] three;
		Integer[] three_2 = new Integer[10];
		List<Long> three_3;
		Integer four = new Integer(23);
		Bar five;
	}
	
	public static class Bar {
		String xxx;
		String zzz;
	}

	public void testPopulate() {

		Map fooMap = new HashMap();
		fooMap.put("one", "ONE");
		fooMap.put("two", "2");
		fooMap.put("four", null);

		List list = new ArrayList();
		list.add("100");
		list.add("200");
		list.add("300");
		fooMap.put("three", list);
		fooMap.put("three_2", list);
		fooMap.put("three_3", list);

		Map map2 = new HashMap();
		map2.put("xxx", "XXX");
		map2.put("zzz", "ZZZ");
		fooMap.put("five", map2);

		
		for (int i = 0; i < 2; i++) {
			Bean bean = new Bean();
			if (i == 0) {
				BeanUtil.populateProperty(bean, "foo", fooMap);
			} else {
				bean.foo = new Foo();
				BeanUtil.populateBean(bean.foo, fooMap);
			}

			// simple properties
			assertNotNull(bean.foo);
			assertEquals("ONE", bean.foo.one);
			assertEquals(2, bean.foo.two);
			assertNull(bean.foo.four);

			// primitive array
			assertEquals(3, bean.foo.three.length);
			assertEquals(100, bean.foo.three[0]);
			assertEquals(200, bean.foo.three[1]);
			assertEquals(300, bean.foo.three[2]);

			// Object array
			assertEquals(10, bean.foo.three_2.length);
			assertEquals(100, bean.foo.three_2[0].intValue());
			assertEquals(200, bean.foo.three_2[1].intValue());
			assertEquals(300, bean.foo.three_2[2].intValue());

			// inner map
			assertEquals("XXX", bean.foo.five.xxx);
			assertEquals("ZZZ", bean.foo.five.zzz);

			// list 2 list
			assertEquals(3, bean.foo.three_3.size());
			assertEquals(100, bean.foo.three_3.get(0).longValue());
			assertEquals(200, bean.foo.three_3.get(1).longValue());
			assertEquals(300, bean.foo.three_3.get(2).longValue());
		}
	}
}
