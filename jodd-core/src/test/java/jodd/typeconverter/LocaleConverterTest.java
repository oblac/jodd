// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.LocaleConverter;
import junit.framework.TestCase;

import java.util.Locale;

public class LocaleConverterTest extends TestCase {

	public void testConversion() {
		LocaleConverter localeConverter = new LocaleConverter();
		
		assertNull(localeConverter.convert(null));

		assertEquals(new Locale("en"), localeConverter.convert("en"));
		assertEquals(new Locale("en", "US"), localeConverter.convert("en_US"));
		assertEquals(new Locale("en","US", "win"), localeConverter.convert("en_US_win"));

		assertEquals(new Locale("en"), localeConverter.convert(new Locale("en")));

	}
}
