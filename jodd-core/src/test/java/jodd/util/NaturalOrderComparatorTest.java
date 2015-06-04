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

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NaturalOrderComparatorTest {

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
		assertEquals(-1, new NaturalOrderComparator<String>().compare("sig[0]", "sig[00]"));
	}

	@Test
	public void testNaturalOrder() {

		String[] strings = new String[]{
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
