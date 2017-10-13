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

package jodd.vtor;

import jodd.vtor.constraint.AssertValidConstraint;
import jodd.vtor.constraint.MinLengthConstraint;
import jodd.vtor.fixtures.Boo;
import jodd.vtor.fixtures.Foo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManualTest {

	@Test
	public void testManualConfig() {

		ValidationContext vctx = new ValidationContext();
		vctx.add(new Check("string", new MinLengthConstraint(2)));

		Vtor vtor = new Vtor();
		vtor.validate(vctx, new Foo());

		assertTrue(vtor.hasViolations());
		List<Violation> vlist = vtor.getViolations();
		assertFalse(vlist.isEmpty());
		assertEquals(1, vlist.size());

		Violation v = vlist.get(0);

		assertEquals("string", v.getName());
		assertEquals("1", v.getInvalidValue());
		assertEquals("string", v.getCheck().getName());
		assertNull(v.getCheck().getMessage());
		assertEquals(MinLengthConstraint.class, v.getConstraint().getClass());

		// new context that contains previous

		ValidationContext vctx2 = new ValidationContext();
		vctx2.add(new Check("foo", new AssertValidConstraint(vctx)));

		vtor = new Vtor();
		vtor.validate(vctx2, new Boo());

		vlist = vtor.getViolations();
		assertFalse(vlist.isEmpty());
		assertEquals(1, vlist.size());


		v = vlist.get(0);

		assertEquals("foo.string", v.getName());
		assertEquals("1", v.getInvalidValue());
		assertEquals("string", v.getCheck().getName());
		assertEquals(MinLengthConstraint.class, v.getConstraint().getClass());

	}

	@Test
	public void testManualAddViolation() {

		ValidationContext vctx = new ValidationContext();
		vctx.add(new Check("string", new MinLengthConstraint(2)));

		Vtor vtor = new Vtor();
		Foo foo = new Foo();
		vtor.validate(vctx, foo);

		vtor.addViolation(new Violation("number", foo, null));


		List<Violation> vlist = vtor.getViolations();
		assertFalse(vlist.isEmpty());
		assertEquals(2, vlist.size());

		Violation v = vlist.get(0);
		assertEquals("string", v.getName());
		assertEquals("1", v.getInvalidValue());
		assertEquals("string", v.getCheck().getName());
		assertEquals(MinLengthConstraint.class, v.getConstraint().getClass());

		v = vlist.get(1);
		assertEquals("number", v.getName());
		assertNull(v.getInvalidValue());
		assertNull(v.getCheck());
		assertNull(v.getConstraint());
	}
}
