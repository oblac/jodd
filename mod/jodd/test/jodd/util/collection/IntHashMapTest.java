// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import junit.framework.TestCase;

public class IntHashMapTest extends TestCase {

	public void testAll() {
		IntHashMap ihm = new IntHashMap();

		for (int i = 0; i < 10000; i++) {
			ihm.put(i, new Integer(i));
		}

		for (int i = 0; i < 10000; i++) {
			assertEquals(((Integer)ihm.get(i)).intValue(), i);
		}
	}

}
