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
import java.util.Comparator;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class FastSortTest {

	static Random rnd = new Random();

	@Test
	public void testSort1() {
		String[] strings = new String[1024 * 100];

		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			strings[i] = RandomString.getInstance().randomAlphaNumeric(10 + rnd.nextInt(100));
		}

		String[] expected = strings.clone();

		jodd.util.sort.FastSort.sort(strings);
		Arrays.sort(expected);

		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			assertEquals(expected[i], strings[i]);
		}
	}

	@Test
	public void testSort2() {
		String[] strings = new String[1024 * 100];

		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			strings[i] = RandomString.getInstance().randomAlphaNumeric(10 + rnd.nextInt(100));
		}

		String[] expected = strings.clone();

		jodd.util.sort.FastSort.sort(strings, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		Arrays.sort(expected, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
			assertEquals(expected[i], strings[i]);
		}
	}
}
