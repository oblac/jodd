// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime.format;

import jodd.datetime.DateTimeStamp;
import jodd.datetime.JDateTime;

/**
 * Abstract formatter for easier {@link JdtFormatter} implementations.
 * <p>
 * For setting date and time, default formatter parses input String against
 * specified format. It extracts parts of input string upon patterns
 * and then each part is converted to a number for a date/time information.
 * It doesn't ignore any non-number character. If conversion fails,
 * <code>null</code> is returned.
 *
 * <p>
 * Getting date time is also user friendly. Specified format may not only
 * contains patterns but also any text. To remove errors in decoding when
 * text may be recognize as one of patterns, format text may be quoted
 * with the special escape sign. Double quote in the text will be decoded
 * as a single quote, of course. 
 * <p>
 *
 * It is not necessary to have parsers for all patterns.
 */
public abstract class AbstractFormatter implements JdtFormatter {

	/**
	 * Available patterns list. Used by {@link #findPattern(char[], int)}
	 * when parsing date time format. Each formatter will have its own set of
	 * patterns, in strictly defined order.
	 */
	protected char[][] patterns;

	/**
	 * Escape character.
	 */
	protected char escapeChar = '\'';


	/**
	 * Converts String array of patterns to char arrays. 
	 */
	protected void preparePatterns(String[] spat) {
		patterns = new char[spat.length][];
		for (int i = 0; i < spat.length; i++) {
			patterns[i] = spat[i].toCharArray();
		}
	}

	/**
	 * Finds the longest pattern in provided format starting from specified position.
	 * All available patterns are stored in {@link #patterns}.
	 *
	 * @param format  date time format to examine
	 * @param i         starting index
	 *
	 * @return  0-based index of founded pattern, or <code>-1</code> if pattern not found
	 */
	protected int findPattern(char[] format, int i) {
		int frmtc_len = format.length;
		boolean match;
		int n, lastn = -1;
		int maxLen = 0;
		for (n = 0; n < patterns.length; n++) {
			char[] curr = patterns[n];					// current pattern from the pattern list
			if (i > frmtc_len - curr.length) {
				continue;
			}
			match = true;
			int delta = 0;
			while (delta < curr.length) {			    // match given pattern
				if (curr[delta] != format[i + delta]) {
					match = false;					    // no match, go to next
					break;
				}
				delta++;
			}
			if (match == true) {				        // match
				if (patterns[n].length > maxLen) {		// find longest match
					lastn = n;
					maxLen = patterns[n].length;
				}
			}
		}
		return lastn;
	}

	// ---------------------------------------------------------------- convert

	/**
	 * Creates a date-time string for founded pattern. Founded patterns
	 * is identified by its {@link #patterns} index.
	 *
	 * @param patternIndex      index of founded pattern
	 * @param jdt               date time information
	 */
	protected abstract String convertPattern(int patternIndex, JDateTime jdt);

	/**
	 * {@inheritDoc}
	 * @see JdtFormatter#convert(JDateTime, String)
	 */
	public String convert(JDateTime jdt, String format) {
		char[] fmtc = format.toCharArray();
		int fmtc_len = fmtc.length;
		StringBuilder result = new StringBuilder(fmtc_len);

		int i = 0;
		while (i < fmtc_len) {
			if (fmtc[i] == escapeChar) {				    // quote founded
				int end = i + 1;
				while (end < fmtc_len) {
					if (fmtc[end] == escapeChar) {		    // second quote founded
						if (end + 1 < fmtc_len) {
							end++;
							if (fmtc[end] == escapeChar) {	// skip double quotes
								result.append(escapeChar);	// and continue
							} else {
								break;
							}
						}
					} else {
						result.append(fmtc[end]);
					}
					end++;
				}
				i = end;
				continue;			// end of quoted string, continue the main loop
			}

			int n = findPattern(fmtc, i);
			if (n != -1) {			// pattern founded
				result.append(convertPattern(n, jdt));
				i += patterns[n].length;
			} else {
				result.append(fmtc[i]);
				i++;
			}
		}
		return result.toString();
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parses value for matched pattern. Founded patterns
	 * is identified by its {@link #patterns} index.
	 * Note that value may represent both integer and decimals.
	 * May throw {@link NumberFormatException}.
	 *
	 * @param patternIndex      index of founded pattern
	 * @param value             value to parse, no spaces or tabs
	 * @param destination       destination to modify
	 */
	protected abstract void parseValue(int patternIndex, String value, DateTimeStamp destination);

	/**
	 * {@inheritDoc}
	 * @see JdtFormatter#parse(String, String)
	 */
	public DateTimeStamp parse(String value, String format) {
		char[] sc = value.toCharArray();
		char[] fc = format.toCharArray();

		int i = 0, j = 0;
		int slen = value.length();
		int tlen = format.length();

		DateTimeStamp time = new DateTimeStamp();
		StringBuilder w = new StringBuilder();
		while (true) {
			int n = findPattern(fc, i);
			if (n != -1) {					// pattern founded
				i += patterns[n].length;
				w.setLength(0);
				char next = 0xFFFF;
				if (i < tlen) {
					next = fc[i];			// next = delimiter
				}
				while ((j < slen) && (sc[j] != next)) {
					char scj = sc[j];
					if ((scj != ' ') && (scj != '\t')) {		// ignore surrounding whitespaces
						w.append(sc[j]);
					}
					j++;
				}
				parseValue(n, w.toString(), time);
			} else  {
				if (fc[i] == sc[j]) {
					j++;
				}
				i++;
			}
			if ((i == tlen) || (j == slen)) {
				break;
			}
		}
		return time;
	}


	// ---------------------------------------------------------------- util

	/**
	 * Prints values 00 - 99.
	 */
	protected String print2(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("Value must be positive: " + value);
		}
		if (value < 10) {
			return '0' + Integer.toString(value);
		}
		if (value < 100) {
			return Integer.toString(value);
		}
		throw new IllegalArgumentException("Value too big: " + value);
	}

	/**
	 * Prints values 00 - 999.
	 */
	protected String print3(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("Value must be positive: " + value);
		}
		if (value < 10) {
			return "00" + Integer.toString(value);
		}
		if (value < 100) {
			return '0' + Integer.toString(value);
		}
		if (value < 1000) {
			return Integer.toString(value);
		}
		throw new IllegalArgumentException("Value too big: " + value);
	}
}
