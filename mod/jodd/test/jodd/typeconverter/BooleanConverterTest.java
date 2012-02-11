// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.BooleanConverter;
import junit.framework.TestCase;

public class BooleanConverterTest extends TestCase {

	public void testConversion() {
		BooleanConverter booleanConverter = new BooleanConverter();

		assertNull(booleanConverter.convert(null));

		assertEquals(Boolean.TRUE, booleanConverter.convert(Boolean.TRUE));

		assertEquals(Boolean.TRUE, booleanConverter.convert("yes"));
		assertEquals(Boolean.TRUE, booleanConverter.convert(" yes "));
		assertEquals(Boolean.TRUE, booleanConverter.convert("YES"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("y"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("Y"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("on"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("ON"));
		assertEquals(Boolean.TRUE, booleanConverter.convert("1"));

		assertEquals(Boolean.FALSE, booleanConverter.convert("no"));
		assertEquals(Boolean.FALSE, booleanConverter.convert(" no "));
		assertEquals(Boolean.FALSE, booleanConverter.convert("NO"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("n"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("N"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("off"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("OFF"));
		assertEquals(Boolean.FALSE, booleanConverter.convert("0"));
	}
}

