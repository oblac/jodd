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

package jodd.petite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InjectionPointFactoryTest {

	@BeforeEach
	public void setUp() throws Exception {
		ipf = new InjectionPointFactory(new PetiteConfig());
	}

	InjectionPointFactory ipf;

	@Test
	public void testDuplicateNamesSpecialCases() {
		String[] s = new String[]{};
		ipf.removeDuplicateNames(s);
		assertEquals(0, s.length);

		s = new String[]{"aaa"};
		ipf.removeDuplicateNames(s);
		assertEquals("aaa", s[0]);

		s = new String[]{null};
		ipf.removeDuplicateNames(s);
		assertNull(s[0]);

		s = new String[]{null, null};
		ipf.removeDuplicateNames(s);
		assertNull(s[0]);
		assertNull(s[1]);
	}

	@Test
	public void testDuplicateNames() {
		String[] s = new String[]{"foo", "foo", "boo", "foo"};
		ipf.removeDuplicateNames(s);
		assertEquals("foo", s[0]);
		assertNull(s[1]);
		assertEquals("boo", s[2]);
		assertNull(s[3]);
	}

	@Test
	public void testDuplicateNames2() {
		String[] s = new String[]{"boo", "foo", "boo", "foo"};
		ipf.removeDuplicateNames(s);
		assertEquals("boo", s[0]);
		assertEquals("foo", s[1]);
		assertNull(s[2]);
		assertNull(s[3]);
	}

	@Test
	public void testDuplicateNames3() {
		String[] s = new String[]{"boo", "boo"};
		ipf.removeDuplicateNames(s);
		assertEquals("boo", s[0]);
		assertNull(s[1]);
	}


}
