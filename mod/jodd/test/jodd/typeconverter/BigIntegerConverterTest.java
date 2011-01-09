// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.BigIntegerConverter;
import junit.framework.TestCase;

import java.math.BigInteger;

public class BigIntegerConverterTest extends TestCase {

	public void testConversion() {
		assertNull(BigIntegerConverter.valueOf(null));

		assertEquals(new BigInteger("12345"), BigIntegerConverter.valueOf(new BigInteger("12345")));
		assertEquals(new BigInteger("12345"), BigIntegerConverter.valueOf("12345"));
		assertEquals(new BigInteger("12345"), BigIntegerConverter.valueOf(Double.valueOf(12345.0D)));
		assertEquals(new BigInteger("123456789"), BigIntegerConverter.valueOf(Long.valueOf(123456789)));
	}
}
