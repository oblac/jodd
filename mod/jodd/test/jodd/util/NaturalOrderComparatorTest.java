// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NaturalOrderComparatorTest extends TestCase {

	public void testNatural202() {
		assertEquals(1, new NaturalOrderComparator<String>().compare("2-02", "2-2"));
	}

	public void testNaturalSig() {
		assertEquals(-1, new NaturalOrderComparator<String>().compare("sig[0]", "sig[1]"));
	}

	public void testNaturalSig00() {
		assertEquals(-1, new NaturalOrderComparator<String>().compare("sig[0]", "sig[00]"));
	}

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

		List<String> list = Arrays.asList(strings.clone());

		Collections.shuffle(list);

		Collections.sort(list, new NaturalOrderComparator<String>());

		for (int i = 0, listSize = list.size(); i < listSize; i++) {
			String s = list.get(i);
			assertEquals(strings[i], s);
		}
	}
}
