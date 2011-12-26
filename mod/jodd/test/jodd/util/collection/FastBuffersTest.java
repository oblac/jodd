// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import junit.framework.TestCase;

import java.util.Arrays;

public class FastBuffersTest extends TestCase {

	public void testCommon() {
		FastIntBuffer fib = new FastIntBuffer(2);
		fib.append(1);
		fib.append(2);
		fib.append(3);
		
		fib.append(new int[] {4,5,6,7,8,9});
		fib.append(new int[] {10,11,12,13,14,15}, 3,1);

		int[] expected = new int[] {1,2,3,4,5,6,7,8,9,13};

		assertEquals(10, fib.size());
		assertTrue(Arrays.equals(expected, fib.toArray()));

		fib.reset();
		assertEquals(0, fib.size());
	}
}
