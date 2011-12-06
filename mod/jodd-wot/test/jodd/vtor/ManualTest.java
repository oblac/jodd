// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.vtor;

import jodd.vtor.constraint.AssertValidConstraint;
import jodd.vtor.constraint.MinLengthConstraint;
import jodd.vtor.data.Boo;
import jodd.vtor.data.Foo;
import junit.framework.TestCase;

import java.util.List;

public class ManualTest extends TestCase {

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
