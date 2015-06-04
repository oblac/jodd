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

import jodd.datetime.JDateTime;
import jodd.vtor.constraint.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConstraintTest {

	@Test
	public void testAssertFalse() {
		assertTrue(AssertFalseConstraint.validate("false"));
		assertTrue(AssertFalseConstraint.validate(null));
		assertTrue(AssertFalseConstraint.validate(Boolean.FALSE));
	}

	@Test
	public void testAssertTrue() {
		assertTrue(AssertTrueConstraint.validate("on"));
		assertTrue(AssertTrueConstraint.validate(null));
		assertTrue(AssertTrueConstraint.validate(Boolean.TRUE));
	}

	@Test
	public void testHasSubstring() {
		assertTrue(HasSubstringConstraint.validate("value", "al", false));
		assertTrue(HasSubstringConstraint.validate("value", "al", true));
	}

	@Test
	public void testMaxConstraint() {
		assertTrue(MaxConstraint.validate("12.1", 12.5));
		assertFalse(MaxConstraint.validate("12.6", 12.5));
	}

	@Test
	public void testMinConstraint() {
		assertTrue(MinConstraint.validate("12.6", 12.5));
		assertFalse(MinConstraint.validate("12.1", 12.5));
	}

	@Test
	public void testTimeAfter() {
		assertTrue(TimeAfterConstraint.validate("2011-05-01 10:11:12.345", new JDateTime("2011-05-01 10:11:12.344")));
		assertFalse(TimeAfterConstraint.validate("2011-05-01 10:11:12.345", new JDateTime("2011-05-01 10:11:12.345")));
	}
}
