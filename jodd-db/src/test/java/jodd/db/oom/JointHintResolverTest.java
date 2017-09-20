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

package jodd.db.oom;

import jodd.db.oom.fixtures.BadBoy;
import jodd.db.oom.fixtures.Girl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JointHintResolverTest {

	@Test
	public void testHints() {
		Girl girl = new Girl();
		BadBoy badBoy = new BadBoy();
		Object[] data = new Object[]{girl, badBoy};

		JoinHintResolver jhr = new JoinHintResolver();
		Object[] result = jhr.join(data, "girl, boy");
		assertEquals(2, result.length);
		assertTrue(result[0] instanceof Girl);
		assertTrue(result[1] instanceof BadBoy);
		badBoy = (BadBoy) result[1];
		assertNull(badBoy.girl);

		jhr = new JoinHintResolver();
		result = jhr.join(data, "boy.girl, boy");
		assertEquals(1, result.length);
		assertTrue(result[0] instanceof BadBoy);
		badBoy = (BadBoy) result[0];
		assertEquals(girl, badBoy.girl);

		girl = new Girl();
		badBoy = new BadBoy();
		data = new Object[]{girl, badBoy, Integer.valueOf(7)};
		jhr = new JoinHintResolver();
		result = jhr.join(data, "boy.girl, boy, boy.girlId");
		assertEquals(1, result.length);
		assertTrue(result[0] instanceof BadBoy);
		badBoy = (BadBoy) result[0];
		assertEquals(girl, badBoy.girl);
		assertEquals(7, badBoy.girlId.intValue());

		girl = new Girl();
		badBoy = new BadBoy();
		data = new Object[]{girl, badBoy, Integer.valueOf(7)};
		jhr = new JoinHintResolver();
		result = jhr.join(data, "boy.girl, boy, girlId");
		assertEquals(2, result.length);
		assertTrue(result[0] instanceof BadBoy);
		badBoy = (BadBoy) result[0];
		assertEquals(girl, badBoy.girl);
		assertNull(badBoy.girlId);
		assertTrue(result[1] instanceof Integer);
	}
}
