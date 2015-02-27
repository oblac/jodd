// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnumTest {

	public enum En {
		ONE, TWO
	}

	@Test
	public void testCastEnums() {
		En en = TypeConverterManager.convertType("ONE", En.class);
		assertEquals(En.ONE, en);
		en = TypeConverterManager.convertType("TWO", En.class);
		assertEquals(En.TWO, en);
	}

}