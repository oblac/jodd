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

package jodd.datetime;

import jodd.datetime.format.Iso8601JdtFormatter;
import jodd.datetime.format.JdtFormat;
import jodd.datetime.format.JdtFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FormatterTest {

	Locale originalLocale;

	@BeforeEach
	public void setUp() throws Exception {
		originalLocale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
	}

	@AfterEach
	public void tearDown() throws Exception {
		Locale.setDefault(originalLocale);
	}

	@Test
	public void testStringConversion() {
		JDateTime jdt = new JDateTime(1975, 1, 1);
		jdt.setTimeZone(TimeZone.getTimeZone("CET"));

		assertEquals("2442413.5", jdt.getJulianDate().toString());
		assertEquals("1975-01-01 00:00:00.000", jdt.toString());

		jdt.setFormat("YYYY.MM.DD");
		assertEquals("1975.01.01", jdt.toString());

		String defaultTemplate = JDateTimeDefault.format;
		JDateTimeDefault.format = "YYYY/MM/DD hh:mm";
		assertEquals("1975.01.01", jdt.toString());

		jdt.setFormat(JDateTimeDefault.format);
		assertEquals("1975/01/01 00:00", jdt.toString());

		assertEquals("01: January (Jan)", jdt.toString("MM: MML (MMS)"));
		assertEquals("01 is 3: Wednesday (Wed)", jdt.toString("DD is D: DL (DS)"));
		jdt.addDay(1);
		assertEquals("02 is 4: Thursday (Thu)", jdt.toString("DD is D: DL (DS)"));
		jdt.addDay(1);
		assertEquals("03 is 5: Friday (Fri)", jdt.toString("DD is D: DL (DS)"));
		jdt.addDay(1);
		assertEquals("04 is 6: Saturday (Sat)", jdt.toString("DD is D: DL (DS)"));
		jdt.addDay(1);
		assertEquals("05 is 7: Sunday (Sun)", jdt.toString("DD is D: DL (DS)"));
		jdt.addDay(1);
		assertEquals("06 is 1: Monday (Mon)", jdt.toString("DD is D: DL (DS)"));
		jdt.addDay(1);
		assertEquals("07 is 2: Tuesday (Tue)", jdt.toString("DD is D: DL (DS)"));

		jdt.addDay(21);
		assertEquals("028 / 05 / W05", jdt.toString("DDD / WW / WWW"));
		assertEquals("AD", jdt.toString("E"));
		assertEquals("Central European Time", jdt.toString("TZL"));
		assertEquals("CET", jdt.toString("TZS"));

		JDateTimeDefault.format = defaultTemplate;
	}


	@Test
	public void testExternalConversion() {
		JdtFormatter fmt = new Iso8601JdtFormatter();
		JDateTime jdt = new JDateTime();
		String s1 = fmt.convert(jdt, "YYYY-MM.DD");
		assertEquals(s1, jdt.toString("YYYY-MM.DD"));
	}


	@Test
	public void testQuotes() {
		JDateTime jdt = new JDateTime(1968, 9, 30);
		assertEquals("SHe WAS Bsample'O'R'N ON 30 September 1968", jdt.toString("SHe 'WAS' 'Bsample''O''R''N' ON DD MML YYYY"));
		assertEquals("5 is a week number and W is a letter", jdt.toString("W is a week number and 'W is a letter"));
		assertEquals("' is a sign, 5 is a week number and W is a letter", jdt.toString("'''' is a sign, W is a week number and 'W' is a letter"));
		assertEquals("1968YYYYY.", jdt.toString("YYYY'YYYYY'."));
		assertEquals("19681968Y.", jdt.toString("YYYYYYYYY'."));
		assertEquals("YY1968YYY", jdt.toString("'YY'YYYY'YY'Y"));
		assertEquals("30 o'day DD30", jdt.toString("DD 'o''day DD'DD"));

	}


	@Test
	public void testParsing() {
		JDateTime jdt = new JDateTime();
		jdt.parse("2003-11-24 23:18:38.173");
		assertEquals("2003-11-24 23:18:38.173", jdt.toString());

		jdt.parse("2003-11-23");
		assertEquals("2003-11-23 00:00:00.000", jdt.toString());

		jdt.parse("2003-14-23");
		assertEquals("2004-02-23 00:00:00.000", jdt.toString());

		jdt.parse("2003-4-3");
		assertEquals("2003-04-03 00:00:00.000", jdt.toString());

		try {
			jdt.parse("2003-4-x");
			fail("error");
		} catch (NumberFormatException nfex) {
			// ignore
		}

		jdt.parse("2003-11-23 13:67");
		assertEquals("2003-11-23 14:07:00.000", jdt.toString());

		jdt.parse("2003-11-23 13:07:55");
		assertEquals("2003-11-23 13:07:55.000", jdt.toString());

		jdt.parse("2003-11-23 13:07:5");
		assertEquals("2003-11-23 13:07:05.000", jdt.toString());

		jdt.parse("123456-7-8");
		assertEquals("123456-07-08 00:00:00.000", jdt.toString());

		jdt.parse("2003-11-23");
		assertEquals("2003-11-23 00:00:00.000", jdt.toString());

		jdt.parse("2001-01-31", "YYYY-MM-***");
		assertEquals("2001-01-01 00:00:00.000", jdt.toString());

	}


	@Test
	public void testFormat() {
		JdtFormat format = new JdtFormat(new Iso8601JdtFormatter(), "YYYY+DD+MM");
		JDateTime jdt = new JDateTime(2002, 2, 22);
		assertEquals("2002+22+02", jdt.toString(format));
		assertEquals(format.convert(jdt), jdt.toString(format));
	}

}
