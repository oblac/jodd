// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.BigDecimalConverter;
import junit.framework.TestCase;

import java.math.BigDecimal;

public class BigDecimalConverterTest extends TestCase {

	public void testConversion() {
		assertNull(BigDecimalConverter.valueOf(null));

		assertEquals(new BigDecimal("1.2345"), BigDecimalConverter.valueOf(new BigDecimal("1.2345")));
		assertEquals(new BigDecimal("1.2345"), BigDecimalConverter.valueOf("1.2345"));
		assertEquals(new BigDecimal("1.2345"), BigDecimalConverter.valueOf(Double.valueOf(1.2345D)));
		assertEquals(new BigDecimal("123456789"), BigDecimalConverter.valueOf(Long.valueOf(123456789)));
	}

}
