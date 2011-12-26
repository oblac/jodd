// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.collection;

import jodd.util.ArraysUtil;
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
	
	public void testChunks() {
		FastIntBuffer fib = new FastIntBuffer(2);

		assertEquals(0, fib.size());
		assertEquals(0, fib.index());
		assertEquals(0, fib.offset());

		fib.append(new int[] {1,2,3});

		assertEquals(3, fib.size());
		assertEquals(1, fib.index());
		assertEquals(1, fib.offset());

		assertTrue(Arrays.equals(new int[] {1,2}, fib.array(0)));
		assertTrue(Arrays.equals(new int[] {3,0,0,0}, fib.array(1)));
		assertTrue(Arrays.equals(new int[] {3}, ArraysUtil.subarray(fib.array(1), 0, fib.offset())));

		fib.append(new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});

		assertEquals(19, fib.size());
		assertEquals(2, fib.index());
		assertEquals(13, fib.array(2).length);
		assertEquals(13, fib.offset());
		assertTrue(Arrays.equals(new int[] {4,5,6,7,8,9,10,11,12,13,14,15,16}, ArraysUtil.subarray(fib.array(2), 0, fib.offset())));

		fib.append(100);

		assertEquals(20, fib.size());
		assertEquals(3, fib.index());
		assertEquals(26, fib.array(3).length);
		assertEquals(1, fib.offset());
	}
	
	public void testInnerBuffers() {
		FastIntBuffer fib = new FastIntBuffer(1);
		
		fib.append(new int[2]);
		assertEquals(1, fib.offset());
		fib.append(new int[4]);
		assertEquals(3, fib.offset());
		fib.append(new int[8]);
		assertEquals(7, fib.offset());
		fib.append(new int[16]);
		assertEquals(15, fib.offset());
		fib.append(new int[32]);
		fib.append(new int[64]);
		fib.append(new int[128]);
		fib.append(new int[256]);
		fib.append(new int[512]);
		fib.append(new int[1024]);
		fib.append(new int[2048]);
		fib.append(new int[4096]);
		fib.append(new int[8192]);
		fib.append(new int[16384]);
		fib.append(new int[32768]);
		fib.append(new int[65536]);

		assertEquals(16, fib.index());
		assertEquals(131070, fib.size());
		assertEquals(65535, fib.offset());
	}
}
