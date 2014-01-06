// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.FloatConverter;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class FloatConverterTest {

	@Test
	public void testConversion() {
		FloatConverter floatConverter = new FloatConverter();

		assertNull(floatConverter.convert(null));

		assertEquals(Float.valueOf(1), floatConverter.convert(Integer.valueOf(1)));
		assertEquals(Float.valueOf((float) 1.73), floatConverter.convert(Double.valueOf(1.73D)));
		assertEquals(Float.valueOf((float) 1.73), floatConverter.convert("1.73"));
		assertEquals(Float.valueOf((float) 1.73), floatConverter.convert(" 1.73 "));
		assertEquals(Float.valueOf((float) 1.73), floatConverter.convert(" +1.73 "));
		assertEquals(Float.valueOf((float) -1.73), floatConverter.convert(" -1.73 "));
		assertEquals(Float.valueOf((float) 1.73), floatConverter.convert(new BigDecimal("1.73")));

		try {
			floatConverter.convert("aaaa");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}
