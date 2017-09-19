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

package jodd.joy.page;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GooNavTest {

	@Test
	public void testPage() {
		GooNav nav = new GooNav(6, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(6, nav.getTo());

		nav = new GooNav(12, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(12, nav.getTo());

		nav = new GooNav(13, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(12, nav.getTo());

		nav = new GooNav(14, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(12, nav.getTo());

		nav = new GooNav(100, 9, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(18, nav.getTo());

		nav = new GooNav(100, 10, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(19, nav.getTo());

		nav = new GooNav(100, 11, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(20, nav.getTo());

		nav = new GooNav(100, 12, 10);
		assertEquals(2, nav.getFrom());
		assertEquals(21, nav.getTo());

		nav = new GooNav(100, 89, 10);
		assertEquals(79, nav.getFrom());
		assertEquals(98, nav.getTo());

		nav = new GooNav(100, 91, 10);
		assertEquals(81, nav.getFrom());
		assertEquals(100, nav.getTo());

	}


}
