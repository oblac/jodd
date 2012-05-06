// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime.format;

import jodd.datetime.DateTimeStamp;
import jodd.datetime.JDateTime;
import jodd.format.Printf;
import jodd.util.LocaleUtil;
import jodd.util.DateFormatSymbolsEx;

import java.util.TimeZone;

/**
 * Default {@link JdtFormatter} uses <b>ISO 8601</b> specification, enhanced by some
 * custom patterns. For more information see:
 * <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO 8601 on Wikipedia</a>
 *
 * <p>
 * Patterns list:
 *
 * <ul>
 * <li>YYYY + year</li>
 * <li>MM + month</li>
 * <li>DD + day of month</li>
 * <li>D - day of week</li>
 * <li>MML - month name (add-on)</li>
 * <li>MMS - month abbreviation (add-on)</li>
 * <li>DL - day of week name (add-on)</li>
 * <li>DS - day of week abbreviation (add-on)</li>
 * <li>hh + hour</li>
 * <li>mm + minute</li>
 * <li>ss + seconds (no milliseconds)</li>
 * <li>mss + milliseconds (add-on)</li>
 * <li>DDD - day of year</li>
 * <li>WW - week of year</li>
 * <li>WWW - week of year with 'W' prefix</li>
 * <li>W - week of month (add-on)</li>
 * <li>E - era</li>
 * <li>TLZ - time zone long</li>
 * <li>TLS - time zone short</li>
 * </ul>
 *
 * <p>
 * Patterns noted with + sign are used both for conversion and parsing.
 * All patterns are used for conversion.
 */
public class DefaultFormatter extends AbstractFormatter {

	public DefaultFormatter() {
		preparePatterns(
				new String[] {
						"YYYY",		// 0  + year
						"MM",		// 1  + month
						"DD",		// 2  + day of month
						"D",		// 3  - day of week
						"MML",		// 4  - month long name
						"MMS",		// 5  - month short name
						"DL",		// 6  - day of week long name
						"DS",		// 7  - day of week short name
						"hh",		// 8  + hour
						"mm",		// 9  + minute
						"ss",		// 10 + seconds
						"mss",		// 11 + milliseconds
						"DDD",		// 12 -	day of year
						"WW",		// 13 - week of year
						"WWW",		// 14 - week of year with 'W' prefix
						"W",		// 15 - week of month
						"E", 		// 16 - era
						"TZL",		// 17 - timezone long name
						"TZS",		// 18 - timezone short name
				}
		);
	}

	@Override
	protected String convertPattern(int patternIndex, JDateTime jdt) {
		DateFormatSymbolsEx dfs = LocaleUtil.getDateFormatSymbols(jdt.getLocale());
		switch (patternIndex) {
			case 0:
				return Printf.str("%~04i", jdt.getYear());
			case 1:
				return print2(jdt.getMonth());
			case 2:
				return print2(jdt.getDay());
			case 3:
				return Integer.toString(jdt.getDayOfWeek());
			case 4:
				return dfs.getMonth(jdt.getMonth() - 1);
			case 5:
				return dfs.getShortMonth(jdt.getMonth() - 1);
			case 6:
				return dfs.getWeekday((jdt.getDayOfWeek() % 7) + 1);
			case 7:
				return dfs.getShortWeekday((jdt.getDayOfWeek() % 7) + 1);
			case 8:
				return print2(jdt.getHour());
			case 9:
				return print2(jdt.getMinute());
			case 10:
				return print2(jdt.getSecond());
			case 11:
				return print3(jdt.getMillisecond());
			case 12:
				return print3(jdt.getDayOfYear());
			case 13:
				return print2(jdt.getWeekOfYear());
			case 14:
				return 'W' + print2(jdt.getWeekOfYear());
			case 15:
				return Integer.toString(jdt.getWeekOfMonth());
			case 16:
				return jdt.getEra() == 1 ? dfs.getAdEra() : dfs.getBcEra();
			case 17:
				return jdt.getTimeZone().getDisplayName(false, TimeZone.LONG, jdt.getLocale());
			case 18:
				return jdt.getTimeZone().getDisplayName(false, TimeZone.SHORT, jdt.getLocale());
			default:
				return new String(patterns[patternIndex]);
		}
	}


	@Override
	protected void parseValue(int patternIndex, String value, DateTimeStamp destination) {
		int v = Integer.parseInt(value);
		switch (patternIndex) {
			case 0:		destination.year = v; break;
			case 1:		destination.month = v; break;
			case 2:		destination.day = v; break;
			case 8:		destination.hour = v; break;
			case 9:		destination.minute = v; break;
			case 10:	destination.second = v; break;
			case 11:	destination.millisecond = v; break;
			default:
				throw new IllegalArgumentException("Parsing template failed: " + new String(patterns[patternIndex]));
		}
	}
}
