// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.BooleanConverter;
import junit.framework.TestCase;

public class BooleanConverterTest extends TestCase {

	public void testConversion() {
		assertNull(BooleanConverter.valueOf(null));

		assertEquals(Boolean.TRUE, BooleanConverter.valueOf(Boolean.TRUE));

		assertEquals(Boolean.TRUE, BooleanConverter.valueOf("yes"));
		assertEquals(Boolean.TRUE, BooleanConverter.valueOf(" yes "));
		assertEquals(Boolean.TRUE, BooleanConverter.valueOf("YES"));
		assertEquals(Boolean.TRUE, BooleanConverter.valueOf("y"));
		assertEquals(Boolean.TRUE, BooleanConverter.valueOf("Y"));
		assertEquals(Boolean.TRUE, BooleanConverter.valueOf("on"));
		assertEquals(Boolean.TRUE, BooleanConverter.valueOf("ON"));
		assertEquals(Boolean.TRUE, BooleanConverter.valueOf("1"));

		assertEquals(Boolean.FALSE, BooleanConverter.valueOf("no"));
		assertEquals(Boolean.FALSE, BooleanConverter.valueOf(" no "));
		assertEquals(Boolean.FALSE, BooleanConverter.valueOf("NO"));
		assertEquals(Boolean.FALSE, BooleanConverter.valueOf("n"));
		assertEquals(Boolean.FALSE, BooleanConverter.valueOf("N"));
		assertEquals(Boolean.FALSE, BooleanConverter.valueOf("off"));
		assertEquals(Boolean.FALSE, BooleanConverter.valueOf("OFF"));
		assertEquals(Boolean.FALSE, BooleanConverter.valueOf("0"));
	}
}

