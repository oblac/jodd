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

public class PageNavTest {

	@Test
	public void testPage() {
		PageNav nav = new PageNav(9, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(9, nav.getTo());

		nav = new PageNav(10, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(10, nav.getTo());


		nav = new PageNav(100, 3, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(10, nav.getTo());

		nav = new PageNav(100, 5, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(10, nav.getTo());

		nav = new PageNav(100, 6, 10);
		assertEquals(1, nav.getFrom());
		assertEquals(10, nav.getTo());

		nav = new PageNav(100, 7, 10);
		assertEquals(2, nav.getFrom());
		assertEquals(11, nav.getTo());

		nav = new PageNav(100, 8, 10);
		assertEquals(3, nav.getFrom());
		assertEquals(12, nav.getTo());

		nav = new PageNav(100, 10, 10);
		assertEquals(5, nav.getFrom());
		assertEquals(14, nav.getTo());


		nav = new PageNav(100, 95, 10);
		assertEquals(90, nav.getFrom());
		assertEquals(99, nav.getTo());

		nav = new PageNav(100, 96, 10);
		assertEquals(91, nav.getFrom());
		assertEquals(100, nav.getTo());

		nav = new PageNav(100, 97, 10);
		assertEquals(91, nav.getFrom());
		assertEquals(100, nav.getTo());

		nav = new PageNav(100, 97, 10);
		assertEquals(91, nav.getFrom());
		assertEquals(100, nav.getTo());

		nav = new PageNav(16, 15, 10);
		assertEquals(7, nav.getFrom());
		assertEquals(16, nav.getTo());

	}


}
