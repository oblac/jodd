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

package jodd.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NaturalOrderComparatorTest {

	@Test
	public void testCaseSensitive() {
		assertTrue("A".compareTo("a") < 0);
		assertTrue(new NaturalOrderComparator<String>().compare("A", "a") < 0);

		assertTrue("a".compareTo("A") > 0);
		assertTrue(new NaturalOrderComparator<String>().compare("a", "A") > 0);

		String[] array = new String[] {"a", "A"};
		Arrays.sort(array);
		assertEquals("A", array[0]);
		assertEquals("a", array[1]);

		array = new String[] {"a", "A"};
		Arrays.sort(array, new NaturalOrderComparator<>(false, false));
		assertEquals("A", array[0]);
		assertEquals("a", array[1]);
	}

	@Test
	public void testNatural202() {
		assertEquals(1, new NaturalOrderComparator<String>().compare("2-02", "2-2"));
	}

	@Test
	public void testNaturalSig() {
		assertEquals(-1, new NaturalOrderComparator<String>().compare("sig[0]", "sig[1]"));
	}

	@Test
	public void testNaturalSig00() {
		assertTrue(new NaturalOrderComparator<String>().compare("sig[0]", "sig[00]") < 0);
	}

	@Test
	public void testNaturalSignatureNumberAndNonNumber() {
		assertTrue(new NaturalOrderComparator<String>().compare("22", "A") < 0);
		assertTrue(new NaturalOrderComparator<String>().compare("22", "!") > 0);

		assertTrue(new NaturalOrderComparator<String>().compare("0", "A") < 0);
		assertTrue(new NaturalOrderComparator<String>().compare("0", "!") > 0);
	}

	/**
	 * The special case when zeros comparison should be ignored, as second number determines
	 * the real order.
	 */
	@Test
	public void testNaturalComparisonSpecialCase() {
		assertTrue(new NaturalOrderComparator<String>().compare("00.1", "0.2") < 0);
	}

	@Test
	public void testNaturalOrder() {

		String[] strings = new String[] {
			"1.2.9.1",
			"1.2.10",
			"1.2.10.5",
			"1.2.10b1",
			"1.2.10p1",
			"1.20.10pre1",
			"2-2",
			"2-02",
			"2-20",
			"20-20",
			"album1page2image10.jpg",
			"album1page2image10b1.jpg",
			"album1page2image10p1.jpg",
			"album1page20image10pre1",
			"album1set2page9photo1.jpg",
			"album1set2page10photo5.jpg",
			"frodo",
			"image9.jpg",
			"image10.jpg",
			"jodd",
			"pic01",
			"pic2",
			"pic02",
			"pic02a",
			"pic3",
			"pic03",
			"pic003",
			"pic0003",
			"pic4",
			"pic 4 else",
			"pic 5",
			"pic 5 something",
			"pic05",
			"pic 6",
			"pic   7",
			"pic100",
			"pic100a",
			"pic120",
			"pic121",
			"pic02000",
			"sig[0]",
			"sig[00]",
			"sig[1]",
			"sig[11]",
			"tito",
			"x7-j8",
			"x7-y7",
			"x7-y08",
			"x8-y8"};

		assertListOrderByShuffling(strings);
	}

	private void assertListOrderByShuffling(String[] strings) {
		Comparator<String> c = new NaturalOrderComparator<>();
		assertListOrder(c, strings);
	}

	private void assertListOrderByShuffling(String[] strings, boolean ignoreCase) {
		Comparator<String> c = new NaturalOrderComparator<>(ignoreCase, true);
		assertListOrder(c, strings);
	}

	private void assertListOrder(Comparator<String> c, String[] strings) {
		int loop = 100;

		while (loop-- > 0) {
			List<String> list = Arrays.asList(strings.clone());

			Collections.shuffle(list);

			list.sort(c);

			for (int i = 0, listSize = list.size(); i < listSize; i++) {
				String s = list.get(i);
				assertEquals(strings[i], s);
			}
		}
	}

	@Test
	public void testNaturalComparatorCommons() {
		NaturalOrderComparator<String> comparator = new NaturalOrderComparator<>();

		assertTrue("image9.jpg".compareTo("image10.jpg") > 0);	// reference

		assertTrue(comparator.compare("image9.jpg", "image10.jpg") < 0);
		assertTrue(comparator.compare("album1set2page9photo1.jpg", "album1set2page10photo5.jpg") < 0);
		assertTrue(comparator.compare("1.2.9.1", "1.2.10.5") < 0);

		assertTrue(comparator.compare("pic 1", "pic2") < 0);

		assertTrue("0]".compareTo("1]") < 0);		// reference
		assertTrue(comparator.compare("0", "1") < 0);
		assertTrue(comparator.compare("0]", "1]") < 0);
		assertTrue(comparator.compare("sig[0]", "sig[1]") < 0);
	}

	@Test
	public void testNaturalComparatorWithZeros() {
		NaturalOrderComparator<String> comparator = new NaturalOrderComparator<>();

		assertTrue("0".compareTo("A") < 0);				// reference
		assertTrue("".compareTo("A") < 0);				// reference

		assertTrue(comparator.compare("", "A") < 0);
		assertTrue(comparator.compare("", "0") < 0);
 		assertTrue(comparator.compare("0", "A") < 0);

		assertTrue(comparator.compare("01", "2") < 0);
		assertTrue(comparator.compare("1", "02") < 0);
		assertTrue(comparator.compare("1", "002") < 0);
		assertTrue(comparator.compare("001", "2") < 0);
		assertTrue(comparator.compare("001", "02") < 0);
		assertTrue(comparator.compare("001", "0002") < 0);
		assertTrue(comparator.compare("001", "00002") < 0);

		assertTrue(comparator.compare("0.1", "0.2") < 0);
		assertTrue(comparator.compare("0.1", "00.2") < 0);
//		assertTrue(comparator.compare("00.1", "0.2") < 0);	see: testNaturalComparisonSpecialCase
		assertTrue(comparator.compare("00.1", "00.2") < 0);

		assertTrue(comparator.compare("00.1", "00.02") < 0);
		assertTrue(comparator.compare("00.01", "00.02") < 0);
		assertTrue(comparator.compare("00.001", "00.02") < 0);
		assertTrue(comparator.compare("00.0001", "00.02") < 0);
		assertTrue(comparator.compare("00.0001", "00.2") < 0);

		assertTrue(comparator.compare("0", "0") == 0);
		assertTrue(comparator.compare("0", "00") < 0);
		assertTrue(comparator.compare("00", "0") > 0);
		assertTrue(comparator.compare("00.00", "0.0") > 0);
		assertTrue(comparator.compare("0.0", "00.00") < 0);
		assertTrue(comparator.compare("0.0", "0.00") < 0);

		assertTrue(comparator.compare("a[0]", "a[0]") == 0);
		assertTrue(comparator.compare("a[0]", "a[00]") < 0);
		assertTrue(comparator.compare("a[01]", "a[002]") < 0);
		assertTrue(comparator.compare("a[03]", "a[002]") > 0);

		assertTrue(comparator.compare("0", "0") == 0);
		assertTrue(comparator.compare("0", "00") < 0);
		assertTrue(comparator.compare("0", "000") < 0);
		assertTrue(comparator.compare("00", "000") < 0);
		assertTrue(comparator.compare("00", "0") > 0);
	}

	@Test
	public void testNaturalComparatorContract() {
		NaturalOrderComparator<String> comparator = new NaturalOrderComparator<>();

		int loop = 1000;

		while(loop-- > 0) {
			String s1 = RandomString.getInstance().randomAscii(2);
			String s2 = RandomString.getInstance().randomAscii(5);
			String s3 = RandomString.getInstance().randomAscii(4);

			assertReflexivity(comparator, s1, s2);
			assertTransitivity(comparator, s1, s2, s3);
		}
	}

	@Test
	public void testNaturalComparatorContract2() {
		NaturalOrderComparator<String> comparator = new NaturalOrderComparator<>();

		int loop = 1000;

		while(loop-- > 0) {
			String s1 = RandomString.getInstance().randomNumeric(2);
			String s2 = RandomString.getInstance().randomNumeric(5);
			String s3 = RandomString.getInstance().randomNumeric(4);

			assertReflexivity(comparator, s1, s2);
			assertTransitivity(comparator, s1, s2, s3);
		}
	}

	@Test
	public void testNaturalComparatorContractCase1() {
		NaturalOrderComparator<String> comparator = new NaturalOrderComparator<>();

		String s1 = "0N";
		String s2 = "Aa";
		String s3 = "4M";

		assertTrue(comparator.compare(s1, s2) < 0);
		assertTrue(comparator.compare(s2, s3) > 0);
		assertTrue(comparator.compare(s1, s3) < 0);

		assertReflexivity(comparator, s1, s2);
		assertTransitivity(comparator, s1, s2, s3);
	}

	@Test
	public void testNaturalComparatorContractCase2() {
		NaturalOrderComparator<String> comparator = new NaturalOrderComparator<>();

		String s1 = "22";
		String s2 = "!tWvL";
		String s3 = "080/";

		assertTrue(comparator.compare(s1, s2) > 0);
		assertTrue(comparator.compare(s2, s3) < 0);
		assertTrue(comparator.compare(s1, s3) < 0);

		assertReflexivity(comparator, s1, s2);
		assertTransitivity(comparator, s1, s2, s3);
	}

	@Test
	public void testNaturalIgnoringLeadingSpaces() {
		String[] list = new String[] {
			"  ignore leading spaces: 2+0",
			"   ignore leading spaces: 2+1",
			" ignore leading spaces: 2-1",
			"ignore leading spaces: 2-2",
		};

		assertListOrderByShuffling(list);
	}

	@Test
	public void testNaturalIgnoreMultipleAdjacentSpaces() {
		String[] list = new String[] {
			"ignore m.a.s   spaces: 2+0",
			"ignore m.a.s    spaces: 2+1",
			"ignore m.a.s  spaces: 2-1",
			"ignore m.a.s spaces: 2-2",
		};

		assertListOrderByShuffling(list);
	}

	@Test
	public void testNaturalAccents() {
		String[] list = new String[] {
			"above",
			"Æon",
			"æony",
			"aether",
			"apple",
			"außen",
			"autumn",
			"bald",
			"Ball",
			"car",
			"Card",
			"e-mail",
			"Évian",
			"evoke",
			"nina",
			"niño",
		};

		assertListOrderByShuffling(list, true);
	}

	private void assertReflexivity(NaturalOrderComparator<String> comparator, String s1, String s2) {
		int one = comparator.compare(s1, s2);
		int two = comparator.compare(s2, s1);

		if (one == 0) {
			assertEquals(0, two);
		} else if (one < 0) {
			assertTrue(two > 0);
		} else {
			assertTrue(two < 0);
		}
	}

	private void assertTransitivity(NaturalOrderComparator<String> comparator, String s1, String s2, String s3) {
		int c12 = comparator.compare(s1,s2);
		int c23 = comparator.compare(s2,s3);
		int c13 = comparator.compare(s1,s3);
		String input = "\ns1: " + s1 + "\ns2: " + s2 + "\ns3: " + s3;

		if (c12 > 0 && c23 > 0) {
			assertTrue(c13 > 0, input);
		}
		else if (c12 < 0 && c23 < 0) {
			assertTrue(c13 < 0, input);
		}
		else if (c12 == 0 & c23 == 0) {
			assertTrue(c13 == 0, input);
		}
	}
}
