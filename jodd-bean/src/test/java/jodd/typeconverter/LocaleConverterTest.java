// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.LocaleConverter;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocaleConverterTest {

	@Test
	public void testConversion() {
		LocaleConverter localeConverter = new LocaleConverter();

		assertNull(localeConverter.convert(null));

		assertEquals(new Locale("en"), localeConverter.convert("en"));
		assertEquals(new Locale("en", "US"), localeConverter.convert("en_US"));
		assertEquals(new Locale("en", "US", "win"), localeConverter.convert("en_US_win"));

		assertEquals(new Locale("en"), localeConverter.convert(new Locale("en")));

	}
}
