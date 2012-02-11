// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.BigDecimalConverter;
import junit.framework.TestCase;

import java.math.BigDecimal;

public class BigDecimalConverterTest extends TestCase {

	public void testConversion() {
		BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

		assertNull(bigDecimalConverter.convert(null));

		assertEquals(new BigDecimal("1.2345"), bigDecimalConverter.convert(new BigDecimal("1.2345")));
		assertEquals(new BigDecimal("1.2345"), bigDecimalConverter.convert("1.2345"));
		assertEquals(new BigDecimal("1.2345"), bigDecimalConverter.convert(" 1.2345 "));
		assertEquals(new BigDecimal("1.2345"), bigDecimalConverter.convert(Double.valueOf(1.2345D)));
		assertEquals(new BigDecimal("123456789"), bigDecimalConverter.convert(Long.valueOf(123456789)));
	}

}
