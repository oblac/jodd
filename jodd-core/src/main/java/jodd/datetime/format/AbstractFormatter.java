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
	 * @param input  date time format to examine
	 * @param i      starting index
	 *
	 * @return  0-based index of founded pattern, or <code>-1</code> if pattern not found
	 */
	protected int findPattern(char[] input, int i) {
		int lastn = -1;
		int maxLen = 0;

		for (int n = 0; n < patterns.length; n++) {
			char[] curr = patterns[n];					// current pattern from the pattern list
			if (i > input.length - curr.length) {
				continue;
			}
			boolean match = true;
			int delta = 0;

				while (delta < curr.length) {			    // match given pattern
				if (curr[delta] != input[i + delta]) {
					match = false;					    // no match, go to next
					break;
				}
				delta++;
			}
			if (match) {				        // match
				if (patterns[n].length > maxLen) {		// find longest match
					lastn = n;
					maxLen = patterns[n].length;
				}
			}
		}
		return lastn;
	}

	/**
	 * Checks if given char is a starting char of a pattern.
	 * Returns char with zero value if next char is actually a pattern.
	 */
	protected char detectSeparatorInPattern(char c) {
		for (char[] curr : patterns) {
			if (curr[0] == c) {
				return 0;
			}
		}
		return c;
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
		char[] valueChars = value.toCharArray();
		char[] formatChars = format.toCharArray();

		int i = 0, j = 0;
		final int valueLen = valueChars.length;
		final int formatLen = formatChars.length;

		final DateTimeStamp time = new DateTimeStamp();
		final StringBuilder sb = new StringBuilder(value.length());

		while (true) {
			int n = findPattern(formatChars, i);

			if (n != -1) {
				// pattern founded

				int patternLen = patterns[n].length;
				i += patternLen;

				sb.setLength(0);

				// detects if next char in the pattern is a separator
				char separator = (i < formatLen) ? detectSeparatorInPattern(formatChars[i]) : 0;

				// proceed value

				if (separator == 0) {
					// no separators - assumes the value length match pattern length, or up to the end of the string
					for (int k = 0; k < patternLen && j < valueLen; k++) {
						sb.append(valueChars[j++]);
					}
				}
				else {
					i++;	// skip separator

					while (j < valueLen) {
						final char c = valueChars[j];
						j++;
						if (c == separator) {
							// there might be whitespaces after the separator!
							while (j < valueLen) {
								final char cc = valueChars[j];
								if ((cc != ' ') && (cc != '\t')) {
									break;
								}
								j++;
							}

							break;
						}
						if ((c != ' ') && (c != '\t')) {		// ignore surrounding whitespaces
							sb.append(c);
						}
					}
				}

				parseValue(n, sb.toString(), time);
			}
			else {
				// pattern not founded, consume the chars

				if (formatChars[i] == valueChars[j]) {
					j++;
				}

				i++;
			}
			if ((i == formatLen) || (j == valueLen)) {
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

	/**
	 * Prints 4 digits and optional minus sign.
	 */
	protected String printPad4(int value) {
		char[] result = new char[4];
		int count = 0;

		if (value < 0) {
			result[count++] = '-';
			value = -value;
		}

		String str = Integer.toString(value);

		if (value < 10) {
			result[count++] = '0';
			result[count++] = '0';
			result[count++] = '0';
			result[count++] = str.charAt(0);
		} else if (value < 100) {
			result[count++] = '0';
			result[count++] = '0';
			result[count++] = str.charAt(0);
			result[count++] = str.charAt(1);
		} else if (value < 1000) {
			result[count++] = '0';
			result[count++] = str.charAt(0);
			result[count++] = str.charAt(1);
			result[count++] = str.charAt(2);
		} else {
			if (count > 0) {
				return '-' + str;
			}
			return str;
		}
		return new String(result, 0, count);
	}
}
