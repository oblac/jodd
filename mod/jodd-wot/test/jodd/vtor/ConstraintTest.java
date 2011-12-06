// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

import jodd.datetime.JDateTime;
import jodd.vtor.constraint.AssertFalseConstraint;
import jodd.vtor.constraint.AssertTrueConstraint;
import jodd.vtor.constraint.HasSubstringConstraint;
import jodd.vtor.constraint.MaxConstraint;
import jodd.vtor.constraint.MinConstraint;
import jodd.vtor.constraint.TimeAfterConstraint;
import junit.framework.TestCase;

public class ConstraintTest extends TestCase {

	public void testAssertFalse() {
		assertTrue(AssertFalseConstraint.validate("false"));
		assertTrue(AssertFalseConstraint.validate(null));
		assertTrue(AssertFalseConstraint.validate(Boolean.FALSE));
	}

	public void testAssertTrue() {
		assertTrue(AssertTrueConstraint.validate("on"));
		assertTrue(AssertTrueConstraint.validate(null));
		assertTrue(AssertTrueConstraint.validate(Boolean.TRUE));
	}

	public void testHasSubstring() {
		assertTrue(HasSubstringConstraint.validate("value", "al", false));
		assertTrue(HasSubstringConstraint.validate("value", "al", true));
	}

	public void testMaxConstraint() {
		assertTrue(MaxConstraint.validate("12.1", 12.5));
		assertFalse(MaxConstraint.validate("12.6", 12.5));
	}

	public void testMinConstraint() {
		assertTrue(MinConstraint.validate("12.6", 12.5));
		assertFalse(MinConstraint.validate("12.1", 12.5));
	}

	public void testTimeAfter() {
		assertTrue(TimeAfterConstraint.validate("2011-05-01 10:11:12.345", new JDateTime("2011-05-01 10:11:12.344")));
		assertFalse(TimeAfterConstraint.validate("2011-05-01 10:11:12.345", new JDateTime("2011-05-01 10:11:12.345")));
	}
}
