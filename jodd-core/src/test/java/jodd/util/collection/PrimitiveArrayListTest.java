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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test primitive arrays
 */
public class PrimitiveArrayListTest {

	@Test
	public void testIntArrayList() {
		IntArrayList list = new IntArrayList();
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());
		list.add(1);
		list.add(2);
		list.add(3);
		assertEquals(3, list.size());
		assertFalse(list.isEmpty());
		assertEquals(1, list.get(0));
		assertEquals(3, list.get(2));
		list.add(0, 4);
		assertEquals(1, list.get(1));
		assertEquals(4, list.get(0));

		assertEquals(1, list.lastIndexOf(1));
		assertEquals(1, list.indexOf(1));

		list.addAll(new int[]{9, 8, 7});
		assertEquals(7, list.size());
		assertEquals(9, list.get(4));
		assertEquals(7, list.get(6));

		list.addAll(3, new int[]{-1, -2});
		assertEquals(9, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-1, list.get(3));
		assertEquals(-2, list.get(4));
		assertEquals(3, list.get(5));
		assertEquals(9, list.get(6));
		assertEquals(7, list.get(8));

		list.set(3, -3);
		assertEquals(-3, list.get(3));

		list.remove(3);
		assertEquals(8, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-2, list.get(3));
		assertEquals(3, list.get(4));
		assertEquals(9, list.get(5));
		assertEquals(7, list.get(7));

		list.removeRange(3, 3);
		assertEquals(8, list.size());
		list.removeRange(3, 5);
		assertEquals(6, list.size());
		assertEquals(2, list.get(2));
		assertEquals(9, list.get(3));
		assertEquals(7, list.get(5));

		list.clear();
		assertEquals(0, list.size());

		list.trimToSize();

		list = new IntArrayList(new int[]{1, 2, 3, 4, 5, 6});
		assertTrue(list.contains(4));
		assertFalse(list.contains(111));
	}

	@Test
	public void testLongArrayList() {
		LongArrayList list = new LongArrayList();
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());
		list.add(1);
		list.add(2);
		list.add(3);
		assertEquals(3, list.size());
		assertFalse(list.isEmpty());
		assertEquals(1, list.get(0));
		assertEquals(3, list.get(2));
		list.add(0, 4);
		assertEquals(1, list.get(1));
		assertEquals(4, list.get(0));

		assertEquals(1, list.lastIndexOf(1));
		assertEquals(1, list.indexOf(1));

		list.addAll(new long[]{9, 8, 7});
		assertEquals(7, list.size());
		assertEquals(9, list.get(4));
		assertEquals(7, list.get(6));

		list.addAll(3, new long[]{-1, -2});
		assertEquals(9, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-1, list.get(3));
		assertEquals(-2, list.get(4));
		assertEquals(3, list.get(5));
		assertEquals(9, list.get(6));
		assertEquals(7, list.get(8));

		list.set(3, -3);
		assertEquals(-3, list.get(3));

		list.remove(3);
		assertEquals(8, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-2, list.get(3));
		assertEquals(3, list.get(4));
		assertEquals(9, list.get(5));
		assertEquals(7, list.get(7));

		list.removeRange(3, 3);
		assertEquals(8, list.size());
		list.removeRange(3, 5);
		assertEquals(6, list.size());
		assertEquals(2, list.get(2));
		assertEquals(9, list.get(3));
		assertEquals(7, list.get(5));

		list.clear();
		assertEquals(0, list.size());

		list.trimToSize();

		list = new LongArrayList(new long[]{1, 2, 3, 4, 5, 6});
		assertTrue(list.contains(4));
		assertFalse(list.contains(111));
	}

	@Test
	public void testByteArrayList() {
		ByteArrayList list = new ByteArrayList();
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());
		list.add((byte) 1);
		list.add((byte) 2);
		list.add((byte) 3);
		assertEquals(3, list.size());
		assertFalse(list.isEmpty());
		assertEquals(1, list.get(0));
		assertEquals(3, list.get(2));
		list.add(0, (byte) 4);
		assertEquals(1, list.get(1));
		assertEquals(4, list.get(0));

		assertEquals(1, list.lastIndexOf((byte) 1));
		assertEquals(1, list.indexOf((byte) 1));

		list.addAll(new byte[]{9, 8, 7});
		assertEquals(7, list.size());
		assertEquals(9, list.get(4));
		assertEquals(7, list.get(6));

		list.addAll(3, new byte[]{-1, -2});
		assertEquals(9, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-1, list.get(3));
		assertEquals(-2, list.get(4));
		assertEquals(3, list.get(5));
		assertEquals(9, list.get(6));
		assertEquals(7, list.get(8));

		list.set(3, (byte) -3);
		assertEquals(-3, list.get(3));

		list.remove(3);
		assertEquals(8, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-2, list.get(3));
		assertEquals(3, list.get(4));
		assertEquals(9, list.get(5));
		assertEquals(7, list.get(7));

		list.removeRange(3, 3);
		assertEquals(8, list.size());
		list.removeRange(3, 5);
		assertEquals(6, list.size());
		assertEquals(2, list.get(2));
		assertEquals(9, list.get(3));
		assertEquals(7, list.get(5));

		list.clear();
		assertEquals(0, list.size());

		list.trimToSize();

		list = new ByteArrayList(new byte[]{1, 2, 3, 4, 5, 6});
		assertTrue(list.contains((byte) 4));
		assertFalse(list.contains((byte) 111));
	}

	@Test
	public void testShortArrayList() {
		ShortArrayList list = new ShortArrayList();
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());
		list.add((short) 1);
		list.add((short) 2);
		list.add((short) 3);
		assertEquals(3, list.size());
		assertFalse(list.isEmpty());
		assertEquals(1, list.get(0));
		assertEquals(3, list.get(2));
		list.add(0, (short) 4);
		assertEquals(1, list.get(1));
		assertEquals(4, list.get(0));

		assertEquals(1, list.lastIndexOf((short) 1));
		assertEquals(1, list.indexOf((short) 1));

		list.addAll(new short[]{9, 8, 7});
		assertEquals(7, list.size());
		assertEquals(9, list.get(4));
		assertEquals(7, list.get(6));

		list.addAll(3, new short[]{-1, -2});
		assertEquals(9, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-1, list.get(3));
		assertEquals(-2, list.get(4));
		assertEquals(3, list.get(5));
		assertEquals(9, list.get(6));
		assertEquals(7, list.get(8));

		list.set(3, (short) -3);
		assertEquals(-3, list.get(3));

		list.remove(3);
		assertEquals(8, list.size());
		assertEquals(2, list.get(2));
		assertEquals(-2, list.get(3));
		assertEquals(3, list.get(4));
		assertEquals(9, list.get(5));
		assertEquals(7, list.get(7));

		list.removeRange(3, 3);
		assertEquals(8, list.size());
		list.removeRange(3, 5);
		assertEquals(6, list.size());
		assertEquals(2, list.get(2));
		assertEquals(9, list.get(3));
		assertEquals(7, list.get(5));

		list.clear();
		assertEquals(0, list.size());

		list.trimToSize();

		list = new ShortArrayList(new short[]{1, 2, 3, 4, 5, 6});
		assertTrue(list.contains((short) 4));
		assertFalse(list.contains((short) 111));
	}

	@Test
	public void testFloatArrayList() {
		FloatArrayList list = new FloatArrayList();
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());
		list.add(1);
		list.add(2);
		list.add(3);
		assertEquals(3, list.size());
		assertFalse(list.isEmpty());
		assertEquals(1, list.get(0), 0.1);
		assertEquals(3, list.get(2), 0.1);
		list.add(0, 4);
		assertEquals(1, list.get(1), 0.1);
		assertEquals(4, list.get(0), 0.1);

		assertEquals(1, list.lastIndexOf(1.0f, 0.1f));
		assertEquals(1, list.indexOf(1f, 0.1f));

		list.addAll(new float[]{9f, 8f, 7f});
		assertEquals(7, list.size());
		assertEquals(9, list.get(4), 0.1);
		assertEquals(7, list.get(6), 0.1);

		list.addAll(3, new float[]{-1, -2});
		assertEquals(9, list.size());
		assertEquals(2, list.get(2), 0.1);
		assertEquals(-1, list.get(3), 0.1);
		assertEquals(-2, list.get(4), 0.1);
		assertEquals(3, list.get(5), 0.1);
		assertEquals(9, list.get(6), 0.1);
		assertEquals(7, list.get(8), 0.1);

		list.set(3, -3);
		assertEquals(-3, list.get(3), 0.1);

		list.remove(3);
		assertEquals(8, list.size());
		assertEquals(2, list.get(2), 0.1);
		assertEquals(-2, list.get(3), 0.1);
		assertEquals(3, list.get(4), 0.1);
		assertEquals(9, list.get(5), 0.1);
		assertEquals(7, list.get(7), 0.1);

		list.removeRange(3, 3);
		assertEquals(8, list.size());
		list.removeRange(3, 5);
		assertEquals(6, list.size());
		assertEquals(2, list.get(2), 0.1);
		assertEquals(9, list.get(3), 0.1);
		assertEquals(7, list.get(5), 0.1);

		list.clear();
		assertEquals(0, list.size());

		list.trimToSize();

		list = new FloatArrayList(new float[]{1, 2, 3, 4, 5, 6});
		assertTrue(list.contains(4f, 0.1f));
		assertFalse(list.contains(111, 0.1f));
	}

	@Test
	public void testDoubleArrayList() {
		DoubleArrayList list = new DoubleArrayList();
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());
		list.add(1);
		list.add(2);
		list.add(3);
		assertEquals(3, list.size());
		assertFalse(list.isEmpty());
		assertEquals(1, list.get(0), 0.1);
		assertEquals(3, list.get(2), 0.1);
		list.add(0, 4);
		assertEquals(1, list.get(1), 0.1);
		assertEquals(4, list.get(0), 0.1);

		assertEquals(1, list.lastIndexOf(1.0, 0.1));
		assertEquals(1, list.indexOf(1, 0.1));

		list.addAll(new double[]{9, 8, 7});
		assertEquals(7, list.size());
		assertEquals(9, list.get(4), 0.1);
		assertEquals(7, list.get(6), 0.1);

		list.addAll(3, new double[]{-1, -2});
		assertEquals(9, list.size());
		assertEquals(2, list.get(2), 0.1);
		assertEquals(-1, list.get(3), 0.1);
		assertEquals(-2, list.get(4), 0.1);
		assertEquals(3, list.get(5), 0.1);
		assertEquals(9, list.get(6), 0.1);
		assertEquals(7, list.get(8), 0.1);

		list.set(3, -3);
		assertEquals(-3, list.get(3), 0.1);

		list.remove(3);
		assertEquals(8, list.size());
		assertEquals(2, list.get(2), 0.1);
		assertEquals(-2, list.get(3), 0.1);
		assertEquals(3, list.get(4), 0.1);
		assertEquals(9, list.get(5), 0.1);
		assertEquals(7, list.get(7), 0.1);

		list.removeRange(3, 3);
		assertEquals(8, list.size());
		list.removeRange(3, 5);
		assertEquals(6, list.size());
		assertEquals(2, list.get(2), 0.1);
		assertEquals(9, list.get(3), 0.1);
		assertEquals(7, list.get(5), 0.1);

		list.clear();
		assertEquals(0, list.size());

		list.trimToSize();

		list = new DoubleArrayList(new double[]{1, 2, 3, 4, 5, 6});
		assertTrue(list.contains(4, 0.1));
		assertFalse(list.contains(111, 0.1));
	}

	@Test
	public void testBooleanArrayList() {
		BooleanArrayList list = new BooleanArrayList();
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());
		list.add(true);
		list.add(false);
		list.add(true);
		assertEquals(3, list.size());
		assertFalse(list.isEmpty());
		assertTrue(list.get(0));
		assertTrue(list.get(2));
		list.add(0, false);
		assertTrue(list.get(1));
		assertFalse(list.get(0));

		assertEquals(3, list.lastIndexOf(true));
		assertEquals(1, list.indexOf(true));

		list.addAll(new boolean[]{true, false, true});
		assertEquals(7, list.size());
		assertTrue(list.get(4));
		assertTrue(list.get(6));

		list.addAll(3, new boolean[]{false, false});
		assertEquals(9, list.size());
		assertFalse(list.get(2));
		assertFalse(list.get(3));
		assertFalse(list.get(4));
		assertTrue(list.get(5));
		assertTrue(list.get(6));
		assertTrue(list.get(8));

		list.set(3, false);
		assertFalse(list.get(3));

		list.remove(3);
		assertEquals(8, list.size());
		assertFalse(list.get(2));
		assertFalse(list.get(3));
		assertTrue(list.get(4));
		assertTrue(list.get(5));
		assertTrue(list.get(7));

		list.removeRange(3, 3);
		assertEquals(8, list.size());
		list.removeRange(3, 5);
		assertEquals(6, list.size());
		assertFalse(list.get(2));
		assertTrue(list.get(3));
		assertTrue(list.get(5));

		list.clear();
		assertEquals(0, list.size());

		list.trimToSize();

		list = new BooleanArrayList(new boolean[]{true, false, true, false, true, false});
		assertTrue(list.contains(false));
	}


}