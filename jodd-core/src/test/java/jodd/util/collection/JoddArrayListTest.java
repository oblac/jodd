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

import jodd.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class JoddArrayListTest {

	/**
	 * Empty list: P = S = E (pivot = start = end)
	 * add: ->
	 * addFirst: -> (!)
	 * removeFirst: ex
	 * removeLast: ex
	 */
	@Test
	public void testGrowCases1() {
		JoddArrayList<String> jal0 = new JoddArrayList<>();

		JoddArrayList<String> jal = (JoddArrayList<String>) jal0.clone();
		jal.add("1");

		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.addFirst("1");

		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		try {
			jal.removeFirst();
			fail("error");
		} catch (Exception ignore) {
		}

		jal = (JoddArrayList<String>) jal0.clone();
		try {
			jal.removeLast();
			fail("error");
		} catch (Exception ignore) {
		}

		checkNulls(jal);
	}

	/**
	 * List: S = X, E = P + 1
	 * add: ->
	 * addFirst: <-
	 * removeFirst: ->
	 * removeLast: <-
	 */
	@Test
	public void testGrowCases2() {
		JoddArrayList<String> jal0 = new JoddArrayList<>();
		addFirst(jal0, 0, 3);
		assertEquals(3, jal0.size());
		assertEquals(16, jal0.buffer.length);
		assertEquals(4, jal0.pivotIndex);
		assertEquals(2, jal0.start);
		assertEquals(5, jal0.end);

		JoddArrayList<String> jal = (JoddArrayList<String>) jal0.clone();
		jal.add("1");

		assertEquals(4, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(2, jal.start);
		assertEquals(6, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.addFirst("1");

		assertEquals(4, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(1, jal.start);
		assertEquals(5, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeFirst();

		assertEquals(2, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(5, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeLast();

		assertEquals(2, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(3, jal.pivotIndex);
		assertEquals(2, jal.start);
		assertEquals(4, jal.end);

		checkNulls(jal);
	}

	/**
	 * List: S = P, E = X
	 * add: ->
	 * addFirst: <-
	 * removeFirst: ->
	 * removeLast: <-
	 */
	@Test
	public void testGrowCases3() {
		JoddArrayList<String> jal0 = new JoddArrayList<>();
		add(jal0, 0, 5);
		assertEquals(5, jal0.size());
		assertEquals(16, jal0.buffer.length);
		assertEquals(4, jal0.pivotIndex);
		assertEquals(4, jal0.start);
		assertEquals(9, jal0.end);

		JoddArrayList<String> jal = (JoddArrayList<String>) jal0.clone();
		jal.add("1");

		assertEquals(6, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(10, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.addFirst("1");

		assertEquals(6, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(9, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeFirst();

		assertEquals(4, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(5, jal.pivotIndex);
		assertEquals(5, jal.start);
		assertEquals(9, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeLast();

		assertEquals(4, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(8, jal.end);

		checkNulls(jal);
	}

	/**
	 * List: S = P, E = S + 1
	 * add: ->
	 * addFirst: <-
	 * removeFirst: <- (!)
	 * removeLast: <-
	 */
	@Test
	public void testGrowCases4() {
		JoddArrayList<String> jal0 = new JoddArrayList<>();
		jal0.add("0");
		assertEquals(1, jal0.size());
		assertEquals(16, jal0.buffer.length);
		assertEquals(4, jal0.pivotIndex);
		assertEquals(4, jal0.start);
		assertEquals(5, jal0.end);

		JoddArrayList<String> jal = (JoddArrayList<String>) jal0.clone();
		jal.add("1");

		assertEquals(2, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(6, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.addFirst("1");

		assertEquals(2, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(5, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeFirst();

		assertEquals(0, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(4, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeLast();

		assertEquals(0, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(4, jal.end);

		checkNulls(jal);
	}

	/**
	 * List: S = P, E = S + 2
	 * add: ->
	 * addFirst: <-
	 * removeFirst: ->
	 * removeLast: <-
	 */
	@Test
	public void testGrowCases5() {
		JoddArrayList<String> jal0 = new JoddArrayList<>();
		add(jal0, 0, 2);
		assertEquals(2, jal0.size());
		assertEquals(16, jal0.buffer.length);
		assertEquals(4, jal0.pivotIndex);
		assertEquals(4, jal0.start);
		assertEquals(6, jal0.end);

		JoddArrayList<String> jal = (JoddArrayList<String>) jal0.clone();
		jal.add("1");

		assertEquals(3, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(7, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.addFirst("1");

		assertEquals(3, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(6, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeFirst();

		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(5, jal.pivotIndex);
		assertEquals(5, jal.start);
		assertEquals(6, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeLast();

		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		checkNulls(jal);
	}


	/**
	 * List: S = E - 2, P = S +1
	 * add: ->
	 * addFirst: <-
	 * removeFirst: ->
	 * removeLast: <-
	 */
	@Test
	public void testGrowCases6() {
		JoddArrayList<String> jal0 = new JoddArrayList<>();
		addFirst(jal0, 0, 2);
		assertEquals(2, jal0.size());
		assertEquals(16, jal0.buffer.length);
		assertEquals(4, jal0.pivotIndex);
		assertEquals(3, jal0.start);
		assertEquals(5, jal0.end);

		JoddArrayList<String> jal = (JoddArrayList<String>) jal0.clone();
		jal.add("1");

		assertEquals(3, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(6, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.addFirst("1");

		assertEquals(3, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(2, jal.start);
		assertEquals(5, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeFirst();

		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		jal = (JoddArrayList<String>) jal0.clone();
		jal.removeLast();

		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(3, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(4, jal.end);

		checkNulls(jal);
	}


	@Test
	public void testAdd1LeftRight() {
		// + +

		JoddArrayList<String> jal = new JoddArrayList<>();
		assertEquals(0, jal.size());

		jal.add("1");
		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		jal.add("2");
		assertEquals(2, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(6, jal.end);

		assertEquals("[1,2]", jal.toString());

		// + -

		jal.clear();
		assertEquals(0, jal.size());

		jal.add("1");
		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		jal.addFirst("2");
		assertEquals(2, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(5, jal.end);

		assertEquals("[2,1]", jal.toString());

		// - +

		jal.clear();
		assertEquals(0, jal.size());

		jal.addFirst("1");
		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		jal.add("2");
		assertEquals(2, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(6, jal.end);

		assertEquals("[1,2]", jal.toString());

		// - -

		jal.clear();
		assertEquals(0, jal.size());

		jal.addFirst("1");
		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		jal.addFirst("2");
		assertEquals(2, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(5, jal.end);

		assertEquals("[2,1]", jal.toString());

		checkNulls(jal);
	}

	@Test
	public void testSimpleAdditionRight() {
		JoddArrayList<String> jal = new JoddArrayList<>();
		assertEquals(0, jal.size());
		assertTrue(jal.isEmpty());
		assertFalse(jal.contains("one"));

		jal.add("one");
		assertFalse(jal.isEmpty());
		assertTrue(jal.contains("one"));

		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		add(jal, 5, 16);

		assertEquals(12, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(16, jal.end);

		// grow right

		jal.add("grow");

		assertEquals(13, jal.size());
		assertEquals(4 + 12 + 10, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(17, jal.end);

		assertTrue(jal.contains("grow"));

		assertEquals("[one,<5>,<6>,<7>,<8>,<9>,<10>,<11>,<12>,<13>,<14>,<15>,grow]", jal.toString());

		assertEquals(0, jal.indexOf("one"));
		assertEquals(1, jal.indexOf("<5>"));
		assertEquals(-1, jal.indexOf("xxx"));
		assertEquals(0, jal.lastIndexOf("one"));
		assertEquals(1, jal.lastIndexOf("<5>"));
		assertEquals(-1, jal.lastIndexOf("xxx"));

		Object[] array = jal.toArray();
		assertEquals(jal.size(), array.length);
		for (int i = 0, arrayLength = array.length; i < arrayLength; i++) {
			assertEquals(array[i], jal.get(i));
		}

		checkNulls(jal);
	}

	@Test
	public void testSimpleAdditionLeft() {
		JoddArrayList<String> jal = new JoddArrayList<>();
		assertEquals(0, jal.size());
		assertTrue(jal.isEmpty());
		assertFalse(jal.contains("one"));

		jal.addFirst("one");
		assertFalse(jal.isEmpty());
		assertTrue(jal.contains("one"));

		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		addFirst(jal, 0, 4);

		assertEquals(5, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(0, jal.start);
		assertEquals(5, jal.end);

		// grow left

		jal.addFirst("grow");

		assertEquals(6, jal.size());
		assertEquals(12 + 4 + 10, jal.buffer.length);
		assertEquals(4 + 10, jal.pivotIndex);
		assertEquals(9, jal.start);
		assertEquals(5 + 10, jal.end);
		assertTrue(jal.contains("grow"));

		assertEquals("[grow,<3>,<2>,<1>,<0>,one]", jal.toString());

		assertEquals(0, jal.indexOf("grow"));
		assertEquals(1, jal.indexOf("<3>"));
		assertEquals(-1, jal.indexOf("xxx"));
		assertEquals(0, jal.lastIndexOf("grow"));
		assertEquals(1, jal.lastIndexOf("<3>"));
		assertEquals(-1, jal.lastIndexOf("xxx"));

		String[] array = jal.toArray(new String[]{});
		assertEquals(jal.size(), array.length);
		for (int i = 0, arrayLength = array.length; i < arrayLength; i++) {
			assertEquals(array[i], jal.get(i));
		}

		checkNulls(jal);
	}

	@Test
	public void testAddAtIndex() {
		JoddArrayList<String> jal = new JoddArrayList<>();
		assertEquals(0, jal.size());
		assertTrue(jal.isEmpty());
		assertFalse(jal.contains("one"));

		jal.add(0, "one");

		assertFalse(jal.isEmpty());
		assertTrue(jal.contains("one"));

		assertEquals(1, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(5, jal.end);

		jal.add(1, "end");

		assertEquals(2, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(6, jal.end);

		jal.add(1, "<3>");
		assertEquals(3, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(7, jal.end);

		assertEquals("[one,<3>,end]", jal.toString());

		jal.add(1, "<4>");
		assertEquals(4, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(8, jal.end);

		assertEquals("[one,<4>,<3>,end]", jal.toString());

		jal.add(0, "<5>");
		assertEquals(5, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(8, jal.end);

		assertEquals("[<5>,one,<4>,<3>,end]", jal.toString());

		jal.add(1, "<6>");
		assertEquals(6, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(2, jal.start);
		assertEquals(8, jal.end);

		assertEquals("[<5>,<6>,one,<4>,<3>,end]", jal.toString());

		jal.add(1, "<7>");
		assertEquals(7, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(1, jal.start);
		assertEquals(8, jal.end);

		assertEquals("[<5>,<7>,<6>,one,<4>,<3>,end]", jal.toString());

		jal.add(2, "<8>");
		jal.add(3, "<88>");
		assertEquals(9, jal.size());
		assertEquals(16 + 10, jal.buffer.length);
		assertEquals(4 + 10, jal.pivotIndex);
		assertEquals(9, jal.start);
		assertEquals(8 + 10, jal.end);

		assertEquals("[<5>,<7>,<8>,<88>,<6>,one,<4>,<3>,end]", jal.toString());

		checkNulls(jal);
	}

	@Test
	public void testAddAll() {
		JoddArrayList<String> jal = new JoddArrayList<>();

		// right

		jal.addAll("1", "2", "3");

		assertEquals(3, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(7, jal.end);

		assertEquals("[1,2,3]", jal.toString());

		jal.addAll("4", "5");

		assertEquals(5, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(9, jal.end);

		jal.addAll(3, "3-1", "3-2", "3-3");

		assertEquals(8, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(12, jal.end);

		assertEquals("[1,2,3,3-1,3-2,3-3,4,5]", jal.toString());

		// left

		jal.addAll(0, "0", "0-1");

		assertEquals(10, jal.size());
		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(2, jal.start);
		assertEquals(12, jal.end);

		assertEquals("[0,0-1,1,2,3,3-1,3-2,3-3,4,5]", jal.toString());

		checkNulls(jal);
	}

	@Test
	public void trimToSize() {
		JoddArrayList<String> jal = new JoddArrayList<>();

		jal.addAll("1", "2", "3", "4", "5");

		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);

		jal.addAll();

		jal.trimToSize();

		assertEquals(5, jal.buffer.length);
		assertEquals(0, jal.start);
		assertEquals(5, jal.end);
		assertEquals(1, jal.pivotIndex);

		checkNulls(jal);
	}

	@Test
	public void testRemoveOne() {
		JoddArrayList<String> jal = new JoddArrayList<>();
		jal.addAll("A", "B", "C", "D", "E");

		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(5, jal.size());
		assertEquals(4, jal.start);
		assertEquals(9, jal.end);

		jal.addAll(0, "0", "1", "2");

		assertEquals(16, jal.buffer.length);
		assertEquals(4, jal.pivotIndex);
		assertEquals(8, jal.size());
		assertEquals(1, jal.start);
		assertEquals(9, jal.end);

		assertEquals("[0,1,2,A,B,C,D,E]", jal.toString());

		// remove left

		jal.remove(0);

		assertEquals(7, jal.size());
		assertEquals(4, jal.pivotIndex);
		assertEquals(2, jal.start);
		assertEquals(9, jal.end);

		assertEquals("[1,2,A,B,C,D,E]", jal.toString());

		jal.remove(1);

		assertEquals(6, jal.size());
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(9, jal.end);

		assertEquals("[1,A,B,C,D,E]", jal.toString());

		// remove right

		jal.remove(4);

		assertEquals(5, jal.size());
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(8, jal.end);

		assertEquals("[1,A,B,C,E]", jal.toString());

		jal.remove(3);

		assertEquals(4, jal.size());
		assertEquals(4, jal.pivotIndex);
		assertEquals(3, jal.start);
		assertEquals(7, jal.end);

		assertEquals("[1,A,B,E]", jal.toString());

		jal.remove(1);
		assertEquals(4, jal.pivotIndex);
		assertEquals(4, jal.start);
		assertEquals(7, jal.end);

		jal.remove(0);
		assertEquals(5, jal.pivotIndex);
		assertEquals(5, jal.start);
		assertEquals(7, jal.end);

		jal.remove(1);
		assertEquals(5, jal.pivotIndex);
		assertEquals(5, jal.start);
		assertEquals(6, jal.end);
		assertEquals(1, jal.size);

		jal.remove(0);
		assertEquals(5, jal.pivotIndex);
		assertEquals(5, jal.start);
		assertEquals(5, jal.end);

		assertEquals(0, jal.size());

		assertEquals("[]", jal.toString());

		checkNulls(jal);
	}

	@Test
	public void testCornerCaseRightNormalise() {
		JoddArrayList<String> jal = new JoddArrayList<>(80);

		add(jal, 0, 60);

		assertEquals(80, jal.buffer.length);
		assertEquals(60, jal.size);
		assertEquals(20, jal.pivotIndex);
		assertEquals(20, jal.start);
		assertEquals(80, jal.end);

		deleteFirst(jal, 50);

		assertEquals(10, jal.size);
		assertEquals(70, jal.pivotIndex);
		assertEquals(70, jal.start);
		assertEquals(80, jal.end);

		// now we gonna ask for grow right

		jal.add("new");

		// normalize should happens!

		assertEquals(80, jal.buffer.length);
		assertEquals(11, jal.size);
		assertEquals(20, jal.pivotIndex);
		assertEquals(18, jal.start);
		assertEquals(29, jal.end);

		assertEquals("[<50>,<51>,<52>,<53>,<54>,<55>,<56>,<57>,<58>,<59>,new]", jal.toString());

		checkNulls(jal);
	}

	@Test
	public void testCornerCaseLeftNormalise() {
		JoddArrayList<String> jal = new JoddArrayList<>(80);

		addFirst(jal, 0, 21);

		assertEquals(80, jal.buffer.length);
		assertEquals(21, jal.size);
		assertEquals(20, jal.pivotIndex);
		assertEquals(0, jal.start);
		assertEquals(21, jal.end);

		deleteLast(jal, 11);

		assertEquals(80, jal.buffer.length);
		assertEquals(10, jal.size);
		assertEquals(9, jal.pivotIndex);
		assertEquals(0, jal.start);
		assertEquals(10, jal.end);

		// now we gonna ask for grow right

		jal.addFirst("new");

		// normalize should happens!

		assertEquals(80, jal.buffer.length);
		assertEquals(11, jal.size);
		assertEquals(20, jal.pivotIndex);
		assertEquals(17, jal.start);
		assertEquals(28, jal.end);

		assertEquals("[new,<20>,<19>,<18>,<17>,<16>,<15>,<14>,<13>,<12>,<11>]", jal.toString());

		checkNulls(jal);
	}

	@Test
	public void testRangeExceptions() {
		JoddArrayList<String> jal = new JoddArrayList<>();

		try {
			jal.get(0);
			fail("error");
		} catch (IndexOutOfBoundsException ignore) {
		}
		try {
			jal.get(1);
			fail("error");
		} catch (IndexOutOfBoundsException ignore) {
		}

		checkNulls(jal);
	}

	@Test
	public void testEmptyList() {
		JoddArrayList<String> jal = new JoddArrayList<>();

		assertEquals(0, jal.size());

		try {
			jal.get(0);
			fail("error");
		} catch (IndexOutOfBoundsException ignore) {
		}

		assertEquals(0, jal.toArray().length);
		assertEquals("[]", jal.toString());

		checkNulls(jal);
	}

	@Test
	public void testClear() {
		JoddArrayList<String> jal = new JoddArrayList<>(80);
		add(jal, 0, 10);
		addFirst(jal, 20, 30);

		assertEquals(20, jal.size);
		assertEquals(10, jal.start);
		assertEquals(20, jal.pivotIndex);
		assertEquals(30, jal.end);

		jal.clear();

		assertEquals(0, jal.size);
		assertEquals(20, jal.start);
		assertEquals(20, jal.pivotIndex);
		assertEquals(20, jal.end);

		checkNulls(jal);
	}

	@Test
	public void testRemove() {
		JoddArrayList<String> jal = new JoddArrayList<>(80);
		add(jal, 0, 10);
		addFirst(jal, 20, 30);

		assertTrue(jal.remove("<1>"));
		assertTrue(jal.remove("<21>"));
		assertFalse(jal.remove("xxx"));

		assertEquals(18, jal.size);
		assertEquals(11, jal.start);
		assertEquals(20, jal.pivotIndex);
		assertEquals(29, jal.end);

		checkNulls(jal);
	}

	@Test
	public void testRemoveAll() {
		JoddArrayList<String> jal = new JoddArrayList<>();
		add(jal, 0, 10);
		addFirst(jal, 20, 30);

		assertEquals(20, jal.size);
		assertEquals(26, jal.buffer.length);		// 16 + growth 10
		assertEquals(4, jal.start);
		assertEquals(14, jal.pivotIndex);

		JoddArrayList<String> collection = new JoddArrayList<>();
		add(collection, 5, 15);
		addFirst(collection, 25, 35);

		assertTrue(jal.removeAll(collection));

		assertEquals("[<24>,<23>,<22>,<21>,<20>,<0>,<1>,<2>,<3>,<4>]", jal.toString());

		assertEquals(10, jal.size);
		assertEquals(26, jal.buffer.length);
		assertEquals(4, jal.start);
		assertEquals(6, jal.pivotIndex);	// 4 + 10/4
		assertEquals(14, jal.end);

		checkNulls(jal);
	}

	@Test
	public void testRetainAll() {
		JoddArrayList<String> jal = new JoddArrayList<>();
		add(jal, 0, 10);
		addFirst(jal, 20, 30);

		assertEquals(20, jal.size);
		assertEquals(26, jal.buffer.length);		// 16 + growth 10
		assertEquals(4, jal.start);
		assertEquals(14, jal.pivotIndex);

		JoddArrayList<String> collection = new JoddArrayList<>();
		add(collection, 5, 15);
		addFirst(collection, 25, 35);

		assertTrue(jal.retainAll(collection));

		assertEquals("[<29>,<28>,<27>,<26>,<25>,<5>,<6>,<7>,<8>,<9>]", jal.toString());

		assertEquals(10, jal.size);
		assertEquals(26, jal.buffer.length);
		assertEquals(4, jal.start);
		assertEquals(6, jal.pivotIndex);	// 4 + 10/4
		assertEquals(14, jal.end);

		checkNulls(jal);
	}

	@Test
	public void testIterator() {
		JoddArrayList<String> jal = new JoddArrayList<>();

		add(jal, 0, 2);
		addFirst(jal, 10, 12);
		add(jal, 20, 22);
		addFirst(jal, 30, 32);

		StringBuilder sb = new StringBuilder();

		Iterator<String> iterator = jal.iterator();
		while (iterator.hasNext()) {
			String next = iterator.next();
			sb.append(next);
		}

		assertEquals("<31><30><11><10><0><1><20><21>", sb.toString());

		try {
			iterator.next();
			fail("error");
		} catch (Exception ignore) {
		}

		iterator = jal.iterator();
		sb.setLength(0);

		while (iterator.hasNext()) {
			String next = iterator.next();
			if (next.contains("1")) {
				iterator.remove();
			} else {
				sb.append(next);
			}
		}

		assertEquals("<30><0><20>", sb.toString());

		checkNulls(jal);
	}

	@Test
	public void testListIterator() {
		JoddArrayList<String> jal = new JoddArrayList<>();

		add(jal, 0, 2);
		addFirst(jal, 10, 12);
		add(jal, 20, 22);
		addFirst(jal, 30, 32);

		StringBuilder sb = new StringBuilder();

		Iterator<String> iterator = jal.listIterator(2);
		while (iterator.hasNext()) {
			String next = iterator.next();
			sb.append(next);
		}

		assertEquals("<11><10><0><1><20><21>", sb.toString());

		try {
			iterator.next();
			fail("error");
		} catch (Exception ignore) {
		}

		iterator = jal.listIterator(2);
		sb.setLength(0);

		while (iterator.hasNext()) {
			String next = iterator.next();
			if (next.contains("1")) {
				iterator.remove();
			} else {
				sb.append(next);
			}
		}

		assertEquals("<0><20>", sb.toString());
		assertEquals("[<31>,<30>,<0>,<20>]", jal.toString());

		// list iterator specifics

		ListIterator li = jal.listIterator(3);

		assertTrue(li.hasPrevious());
		assertTrue(li.hasNext());
		assertEquals(3, li.nextIndex());
		assertEquals(2, li.previousIndex());

		li.next();
		li.previous();
		li.next();

		assertEquals(4, li.nextIndex());
		assertEquals(3, li.previousIndex());

		assertFalse(li.hasNext());

		// again

		li = jal.listIterator();

		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("<31>");
		arrayList.add("<30>");
		arrayList.add("<0>");
		arrayList.add("<20>");
		ListIterator li2 = arrayList.listIterator();

		assertFalse(li.hasPrevious());
		assertFalse(li2.hasPrevious());

		li.next(); li2.next();
		li.add("A"); li2.add("A");
		li.next(); li2.next();
		li.remove();	li2.remove(); // removes last returned one
		assertEquals("[<31>,A,<0>,<20>]", jal.toString());
		assertEquals("[<31>,A,<0>,<20>]", StringUtil.remove(arrayList.toString(), ' '));

		li.previous(); li2.previous();
		li.previous(); li2.previous();
		li.remove(); li2.remove();

		assertEquals("[A,<0>,<20>]", jal.toString());
		assertEquals("[A,<0>,<20>]", StringUtil.remove(arrayList.toString(), ' '));

		checkNulls(jal);
	}

	@Test
	public void testRandomAccess() {
		JoddArrayList<Integer> jal = new JoddArrayList<>();
		ArrayList<Integer> al = new ArrayList<>();

		Random rnd = new Random();

		for (int i = 0; i < 1000; i++) {
			int operation = rnd.nextInt(4);

			switch (operation) {
				case 0 : jal.add(i); al.add(i); break;
				case 1 : jal.addFirst(i); al.add(0, i); break;
				case 2 : {
					if (jal.size() == 0) {
						continue;
					}
					int index = rnd.nextInt(jal.size);
					jal.add(index, i); al.add(index, i);
					break;
				}
				case 3: {
					if (jal.size() == 0) {
						continue;
					}
					int index = rnd.nextInt(jal.size);
					jal.remove(index); al.remove(index);
					break;
				}
			}

		}

		assertArrayEquals(al.toArray(), jal.toArray());
	}

	@Test
	public void testSpecialCase1() {
		JoddArrayList jal = new JoddArrayList();

		jal.add(0, "0");
		jal.add(jal.size() - 1, "1");
		jal.add(jal.size() - 1, "2");
		jal.add(jal.size() - 1, "3");
		jal.add(0, "4");
		jal.add(jal.size() - 1, "5");
	}

	// ---------------------------------------------------------------- util

	protected void add(JoddArrayList<String> jal, int start, int end) {
		for (int i = start; i < end; i++) {
			jal.add("<" + i + '>');
		}
	}
	protected void addFirst(JoddArrayList<String> jal, int start, int end) {
		for (int i = start; i < end; i++) {
			jal.addFirst("<" + i + '>');
		}
	}
	protected void deleteLast(JoddArrayList<String> jal, int count) {
		while (count-->0) {
			jal.removeLast();
		}
	}
	protected void deleteFirst(JoddArrayList<String> jal, int count) {
		while (count-->0) {
			jal.removeFirst();
		}
	}

	private void checkNulls(JoddArrayList<String> jal) {
		int start = jal.start;
		int end = jal.end;

		for (int i = 0; i < start; i++) {
			assertNull(jal.buffer[i]);
		}
		for (int i = end; i < jal.buffer.length; i++) {
			assertNull(jal.buffer[i]);
		}
	}

}
