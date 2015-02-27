// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.mutable.MutableDouble;
import jodd.typeconverter.impl.MutableDoubleConverter;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class MutableDoubleConverterTest {

	@Test
	public void testConversion() {
		MutableDoubleConverter mutableDoubleConverter = (MutableDoubleConverter) TypeConverterManager.lookup(MutableDouble.class);

		assertNull(mutableDoubleConverter.convert(null));

		assertEquals(new MutableDouble(1.73), mutableDoubleConverter.convert(new MutableDouble(1.73)));
		assertEquals(new MutableDouble(1), mutableDoubleConverter.convert(Integer.valueOf(1)));
		assertEquals(new MutableDouble(1.73), mutableDoubleConverter.convert(Double.valueOf(1.73D)));
		assertEquals(new MutableDouble(1.73), mutableDoubleConverter.convert("1.73"));
		assertEquals(new MutableDouble(1.73), mutableDoubleConverter.convert(" 1.73 "));
		assertEquals(new MutableDouble(1.73), mutableDoubleConverter.convert(new BigDecimal("1.73")));

		try {
			mutableDoubleConverter.convert("aaaa");
			fail();
		} catch (TypeConversionException ignore) {
		}
	}
}

