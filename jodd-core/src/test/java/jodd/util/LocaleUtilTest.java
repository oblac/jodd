// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import org.junit.Test;

import java.text.DateFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.*;

public class LocaleUtilTest {

	@Test
	public void testLocaleUtil() {
		Locale locale1 = LocaleUtil.getLocale("fr", "FR");
		Locale locale2 = LocaleUtil.getLocale("fr_FR");
		assertSame(locale1, locale2);

		locale1 = LocaleUtil.getLocale("en");
		locale2 = LocaleUtil.getLocale("en_EN");
		assertNotSame(locale1, locale2);

		DateFormatSymbols dfs = LocaleUtil.getDateFormatSymbols(locale2);
		assertEquals("January", dfs.getMonths()[0]);


	}
}
