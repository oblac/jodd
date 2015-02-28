// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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
