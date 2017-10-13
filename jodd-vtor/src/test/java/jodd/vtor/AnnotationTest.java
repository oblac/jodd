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

import jodd.vtor.constraint.TimeAfterConstraint;
import jodd.vtor.fixtures.Tad;
import jodd.vtor.fixtures.Woo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationTest {

	@Test
	public void testAnn() {
		Vtor vtor = new Vtor();
		vtor.validate(new Woo());
		List<Violation> v = vtor.getViolations();
		assertEquals(1, v.size());

		vtor.resetViolations();
		vtor.useProfiles("p1", "p2");
		vtor.validate(new Woo());
		v = vtor.getViolations();
		assertEquals(2, v.size());

		vtor.resetViolations();
		vtor.useProfiles("default", "p1", "p2");
		vtor.validate(new Woo());
		v = vtor.getViolations();
		assertEquals(3, v.size());
	}

	@Test
	public void testTime() {
		Vtor vtor = new Vtor();
		vtor.validate(new Tad());

		assertTrue(vtor.hasViolations());
		List<Violation> v = vtor.getViolations();

		assertEquals(3, v.size());

		assertEquals(TimeAfterConstraint.class, v.get(0).getConstraint().getClass());
	}
}
