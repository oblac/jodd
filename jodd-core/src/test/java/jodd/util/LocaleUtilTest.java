// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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

		DateFormatSymbolsEx dfs = LocaleUtil.getDateFormatSymbols(locale2);
		DateFormatSymbols dfsJava = new DateFormatSymbols(locale2);
		assertEquals(dfs.getMonth(0), dfsJava.getMonths()[0]);
		assertEquals(dfs.getWeekday(1), dfsJava.getWeekdays()[1]);
		assertEquals(dfs.getShortMonth(2), dfsJava.getShortMonths()[2]);

		locale1 = LocaleUtil.getLocale("en");
		locale2 = LocaleUtil.getLocale("en_EN");
		assertNotSame(locale1, locale2);

		dfs = LocaleUtil.getDateFormatSymbols(locale2);
		dfsJava = new DateFormatSymbols(locale2);
		assertEquals(dfs.getMonth(0), dfsJava.getMonths()[0]);
		assertEquals(dfs.getWeekday(1), dfsJava.getWeekdays()[1]);
		assertEquals(dfs.getShortMonth(2), dfsJava.getShortMonths()[2]);
	}
}
