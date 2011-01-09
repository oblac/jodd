// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.datetime.JDateTime;
import jodd.typeconverter.impl.JDateTimeConverter;

public class JDateTimeConverterTest extends BaseTestCase {

	public void testConversion() {
		assertNull(JDateTimeConverter.valueOf(null));

		assertEquals(new JDateTime(2010, 10, 10), JDateTimeConverter.valueOf(new JDateTime(2010, 10, 10)));
		assertEquals(new JDateTime(123456), JDateTimeConverter.valueOf(Integer.valueOf(123456)));
		assertEquals(new JDateTime(2010, 10, 20, 10, 11, 12, 456), JDateTimeConverter.valueOf("2010-10-20 10:11:12.456"));
	}
}
