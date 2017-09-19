// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.util;

import org.junit.jupiter.api.Test;

import java.text.DateFormatSymbols;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

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
