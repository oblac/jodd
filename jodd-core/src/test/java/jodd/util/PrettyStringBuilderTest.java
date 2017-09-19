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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrettyStringBuilderTest {

	@Test
	public void testList() {
		List<String> l = new ArrayList<>();
		l.add("One");
		l.add("Two");
		assertEquals("(One,Two)", new PrettyStringBuilder().toString(l));
	}

	@Test
	public void testMap() {
		Map m = new LinkedHashMap();
		m.put(1, "One");
		m.put(2, "Two");
		assertEquals("{1:One,2:Two}", new PrettyStringBuilder().toString(m));
	}

	@Test
	public void testArray() {
		int[] arr = new int[]{1, 2, 3};
		assertEquals("[1,2,3]", new PrettyStringBuilder().toString(arr));
	}

	@Test
	public void testMax() {
		PrettyStringBuilder psb = new PrettyStringBuilder().maxItemsToShow(3);
		int[] arr = new int[]{1, 2, 3};
		assertEquals("[1,2,3]", psb.toString(arr));
		arr = new int[]{1, 2, 3, 4};
		assertEquals("[1,2,3,...]", psb.toString(arr));
	}

	@Test
	public void testDeep() {
		List l = new ArrayList();
		l.add("One");
		l.add(new int[]{1, 2,});
		assertEquals("(One,[1,2])", new PrettyStringBuilder().toString(l));
	}
}
