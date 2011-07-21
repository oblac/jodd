// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.DateConverter;

import java.util.Date;

public class DateConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(DateConverter.valueOf(null));

		assertEquals(new Date(885858), DateConverter.valueOf("885858"));
		assertEquals(new Date(123), DateConverter.valueOf(Integer.valueOf(123)));

	}
}

