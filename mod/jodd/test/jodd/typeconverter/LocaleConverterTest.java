// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.LocaleConverter;
import junit.framework.TestCase;

import java.util.Locale;

public class LocaleConverterTest extends TestCase {

	public void testConversion() {
		assertNull(LocaleConverter.valueOf(null));

		assertEquals(new Locale("en"), LocaleConverter.valueOf("en"));
		assertEquals(new Locale("en", "US"), LocaleConverter.valueOf("en_US"));
		assertEquals(new Locale("en","US", "win"), LocaleConverter.valueOf("en_US_win"));

		assertEquals(new Locale("en"), LocaleConverter.valueOf(new Locale("en")));

	}
}
