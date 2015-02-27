// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.mutable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MutableTest {

	@Test
	public void testMutableInteger() {
		MutableInteger m = new MutableInteger();

		assertTrue(m instanceof Number);
		m.setValue(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertEquals(0, m.compareTo((MutableInteger) m2));
	}

	@Test
	public void testMutableLong() {
		MutableLong m = new MutableLong();

		assertTrue(m instanceof Number);
		m.setValue(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertEquals(0, m.compareTo((MutableLong) m2));
	}

	@Test
	public void testMutableShort() {
		MutableShort m = new MutableShort();

		assertTrue(m instanceof Number);
		m.setValue(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertEquals(0, m.compareTo((MutableShort) m2));
	}

	@Test
	public void testMutableByte() {
		MutableByte m = new MutableByte();

		assertTrue(m instanceof Number);
		m.setValue(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertEquals(0, m.compareTo((MutableByte) m2));
	}

	@Test
	public void testMutableFloat() {
		MutableFloat m = new MutableFloat();

		assertTrue(m instanceof Number);
		m.setValue(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertEquals(0, m.compareTo((MutableFloat) m2));
	}

	@Test
	public void testMutableDouble() {
		MutableDouble m = new MutableDouble();

		assertTrue(m instanceof Number);
		m.setValue(27);
		assertEquals(27, m.intValue());
		assertEquals(27, m.longValue());
		assertEquals(27, m.shortValue());
		assertEquals(27, m.byteValue());
		assertEquals(27, m.floatValue(), 0.1);
		assertEquals(27, m.doubleValue(), 0.1);

		Number m2 = m.clone();

		assertEquals(m2, m);
		assertEquals(0, m.compareTo((MutableDouble) m2));
	}

	@Test
	public void testMutableBoolean() {
		MutableBoolean m = new MutableBoolean();

		m.setValue(true);
		assertEquals(true, m.getValue());

		Object m2 = m.clone();

		assertEquals(m2, m);
		assertEquals(0, m.compareTo((MutableBoolean) m2));
	}

}