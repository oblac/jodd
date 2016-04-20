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

package jodd.format;

import jodd.util.StringPool;

import java.math.BigInteger;

/**
 * Fast simple and yet useful formatting.
 */
public class PrintfFormat {

	protected int width;
	protected int precision;
	protected StringBuilder pre;
	protected StringBuilder post;
	protected boolean leadingZeroes;
	protected boolean showPlus;
	protected boolean alternate;
	protected boolean showSpace;
	protected boolean leftAlign;
	protected boolean groupDigits;
	protected char fmt;	                // one of cdeEfgGiosxXos...
	protected boolean countSignInLen;
	private static final BigInteger bgInt = new BigInteger("9223372036854775808");  // 2^63

	/**
	 * Formats a number in a printf format, like C.
	 *
	 * @param s      the format string following printf format string
	 *               The string has a prefix, a format code and a suffix. The prefix and suffix
	 *               become part of the formatted output. The format code directs the
	 *               formatting of the (single) parameter to be formatted. The code has the
	 *               following structure
	 *               <ul>
	 *               <li> a <b>%</b> (required)
	 *
	 *               <li> a modifier (optional)
	 *               <dl>
	 *               <dt> + <dd> forces display of + for positive numbers
	 *               <dt> ~ <dd> do not count leading + or - in length
	 *               <dt> 0 <dd> show leading zeroes
	 *               <dt> - <dd> align left in the field
	 *               <dt> space <dd> prepend a space in front of positive numbers
	 *               <dt> # <dd> use "alternate" format. Add 0 or 0x for octal or hexadecimal numbers;
	 *               add 0b for binary numbers.  Don't suppress trailing zeroes in general floating 
	 *               point format.
	 *               <dt> , <dd> groups decimal values by thousands (for 'diuxXb' formats)
	 *               </dl>
	 *
	 *               <li> an integer denoting field width (optional)
	 *
	 *               <li> a period (<b>.</b>) followed by an integer denoting precision (optional)
	 *
	 *               <li> a format descriptor (required)
	 *               <dl>
	 *               <dt>f <dd> floating point number in fixed format,
	 *               <dt>e, E <dd> floating point number in exponential notation (scientific format).
	 *               The E format results in an uppercase E for the exponent (1.14130E+003), the e
	 *               format in a lowercase e,
	 *               <dt>g, G <dd> floating point number in general format (fixed format for small
	 *               numbers, exponential format for large numbers). Trailing zeroes are suppressed.
	 *               The G format results in an uppercase E for the exponent (if any), the g format
	 *               in a lowercase e,.
	 *               <dt>d, i <dd> signed long and integer in decimal,
	 *               <dt>u <dd> unsigned long or integer in decimal,
	 *               <dt>x <dd> unsigned long or integer in hexadecimal,
	 *               <dt>o <dd> unsigned long or integer in octal,
	 *               <dt>b <dd> unsigned long or integer in binary,
	 *               <dt>s <dd> string (actually, <code>toString()</code> value of an object),
	 *               <dt>c <dd> character,
	 *               <dt>l, L <dd> boolean in lower or upper case (for booleans and int/longs),
	 *               <dt>p <dd> identity hash code of an object (pointer :).
	 *               </dl>
	 *               </ul>
	 */
	public PrintfFormat(String s) {
		init(s, 0);
	}

	/**
	 * For internal use with {@link #init(String, int)} and {@link #reinit(String)}.
	 */
	protected PrintfFormat() {
	}

	protected PrintfFormat reinit(String s) {
		if (pre == null) {
			init(s, 0);
		} else {
			init(s, pre.length());
		}
		return this;
	}

	protected void init(String s, int i) {
		width = 0;
		precision = -1;
		pre = (i == 0 ? new StringBuilder() : new StringBuilder(s.substring(0, i)));
		post = new StringBuilder();
		leadingZeroes = false;
		showPlus = false;
		alternate = false;
		showSpace = false;
		leftAlign = false;
		countSignInLen = true;
		fmt = ' ';

		int length = s.length();
		int parseState;                 // 0 = prefix, 1 = flags, 2 = width, 3 = precision, 4 = format, 5 = end

		// 0: parse string prefix upto first '%'.
		while (true) {
			if (i >= length) {
				throw new IllegalArgumentException("Format string requires '%'.");
			}
			char c = s.charAt(i);
			if (c != '%') {
				pre.append(c);
				i++;
				continue;
			}
			if (i >= length - 1) {
				throw new IllegalArgumentException("Format string can not end with '%'.");
			}
			if (s.charAt(i + 1) == '%') {       // double '%%'
				pre.append('%');
				i += 2;
				continue;
			}
			//parseState = 1;                 // single $ founded
			i++;
			break;
		}

		// 1: parse flags
		flagsloop:
		//while (parseState == 1) {
		while (true) {
			if (i >= length) {
				parseState = 5;
				break;
			}
			char c = s.charAt(i);
			switch (c) {
				case ' ': showSpace = true; break;
				case '-': leftAlign = true; break;
				case '+': showPlus = true; break;
				case '0': leadingZeroes = true; break;
				case '#': alternate = true; break;
				case '~': countSignInLen = false; break;
				case ',': groupDigits = true; break;
				default:
					parseState = 2;
					break flagsloop;
			}
			i++;
		}

		// 2: parse width
		while (parseState == 2) {
			if (i >= length) {
				parseState = 5;
				break;
			}
			char c = s.charAt(i);
			if ((c >= '0') && (c <= '9')) {
				width = (width * 10) + s.charAt(i) - '0';
				i++;
				continue;
			}
			if (s.charAt(i) == '.') {
				parseState = 3;
				precision = 0;
				i++;
			} else {
				parseState = 4;
			}
			break;
		}

		// 3: parse precision
		while (parseState == 3) {
			if (i >= length) {
				parseState = 5;
				break;
			}
			char c = s.charAt(i);
			if ((c >= '0') && (c <= '9')) {
				precision = (precision * 10) + s.charAt(i) - '0';
				i++;
				continue;
			}
			parseState = 4;
			break;
		}

		// 4: parse format
		if (parseState == 4) {
			if (i < length) {
				fmt = s.charAt(i);
				i++;				
//			} else {
//				parseState = 5;
			}
		}

		// append suffix
		if (i < length) {
			post.append(s.substring(i, length));
		}
	}

	/**
	 * Formats a double with exp format.
	 */
	protected String expFormat(double d) {
		StringBuilder f = new StringBuilder();
		int e = 0;
		double dd = d;
		double factor = 1;

		if (d != 0) {
			while (dd > 10) {
				e++;
				factor /= 10;
				dd /= 10;
			}
			while (dd < 1) {
				e--;
				factor *= 10;
				dd *= 10;
			}
		}
		if (((fmt == 'g') || (fmt == 'G')) && (e >= -4) && (e < precision)) {
			return fixedFormat(d);
		}

		d *= factor;
		f.append(fixedFormat(d));

		if (fmt == 'e' || fmt == 'g') {
			f.append('e');
		} else {
			f.append('E');
		}

		StringBuilder p = new StringBuilder("000");
		if (e >= 0) {
			f.append('+');
			p.append(e);
		} else {
			f.append('-');
			p.append(-e);
		}

		char[] data = new char[3];
		p.getChars(p.length() - 3, p.length(), data, 0);
		return f.append(data).toString();
	}

	/**
	 * Formats a double with fixed format.
	 */
	protected String fixedFormat(double d) {
		boolean removeTrailing = (fmt == 'G' || fmt == 'g') && !alternate;

		// remove trailing zeroes and decimal point
		if (d > 0x7FFFFFFFFFFFFFFFL) {
			return expFormat(d);
		}
		if (precision == 0) {
			return Long.toString(Math.round(d));
		}

		long whole = (long) d;
		double fr = d - whole; // fractional part

		if (fr >= 1 || fr < 0) {
			return expFormat(d);
		}

		double factor = 1;
		StringBuilder leadingZeroesStr = new StringBuilder();

		for (int i = 1; i <= precision && factor <= 0x7FFFFFFFFFFFFFFFL; i++) {
			factor *= 10;
			leadingZeroesStr.append('0');
		}

		long l = Math.round(factor * fr);
		if (l >= factor) {
			l = 0;
			whole++;
		}

		String z = leadingZeroesStr.toString() + l;
		z = '.' + z.substring(z.length() - precision, z.length());

		if (removeTrailing) {
			int t = z.length() - 1;
			while (t >= 0 && z.charAt(t) == '0') {
				t--;
			}
			if (t >= 0 && z.charAt(t) == '.') {
				t--;
			}
			z = z.substring(0, t + 1);
		}
		return whole + z;
	}

	/**
	 * Pads the value with spaces and adds prefix and suffix.
	 */
	protected String pad(String value) {
		String spaces = repeat(' ', width - value.length());
		if (leftAlign) {
			return pre + value + spaces + post;
		} else {
			return pre + spaces + value + post;
		}
	}

	/**
	 * Returns new string created by repeating a single character.
	 */
	protected static String repeat(char c, int n) {
		if (n <= 0) {
			return (StringPool.EMPTY);
		}
		char[] buffer = new char[n];
		for (int i = 0; i < n; i++) {
			buffer[i] = c;
		}
		return new String(buffer);
	}
	
	private String getAltPrefixFor(char fmt, String currentPrefix) {
		switch(fmt) {
			case 'x':
				return "0x";
			case 'X':
				return "0X";
			case 'b':
				return "0b";
			case 'B':
				return "0B";
			default:
				return currentPrefix;
		}
	}

	protected String sign(int s, String r) {
		String p = StringPool.EMPTY;

		if (s < 0) {
			p = StringPool.DASH;
		} else if (s > 0) {
			if (showPlus) {
				p = StringPool.PLUS;
			} else if (showSpace) {
				p = StringPool.SPACE;
			}
		} else {
			if (alternate) {
				if (fmt == 'o' && r.length() > 0 && r.charAt(0) != '0') {
					p = "0";
				} else {
					p = getAltPrefixFor(fmt, p);
				}
			}
		}

		int w = 0;

		if (leadingZeroes) {
			w = width;
		} else if ((fmt == 'u' || fmt == 'd' || fmt == 'i' || fmt == 'x' || fmt == 'X' || fmt == 'o') && precision > 0) {
			w = precision;
		}

		if (countSignInLen) {
			return p + repeat('0', w - p.length() - r.length()) + r;
		} else {
			return p + repeat('0', w - r.length()) + r;
		}
	}

	/**
	 * Groups numbers by inserting 'separator' after every group of 'size' digits,
	 * starting from the right.
	 */
	protected String groupDigits(String value, int size, char separator) {
		if (!groupDigits) {
			return value;
		}
		StringBuilder r = new StringBuilder(value.length() + 10);
		int ndx = 0;
		int len = value.length() - 1;
		int mod = len % size;
		while (ndx < len) {
			r.append(value.charAt(ndx));
			if (mod == 0) {
				r.append(separator);
				mod = size;
			}
			mod--;
			ndx++;
		}
		r.append(value.charAt(ndx));
		return r.toString();
	}



	// ---------------------------------------------------------------- public form methods

	/**
	 * Formats a character into a string (like sprintf in C).
	 */
	public String form(char value) {
		switch(fmt) {
			case 'c':
				return alternate ? "\\u" + Integer.toHexString((int) value & 0xFFFF) : pad(String.valueOf(value));
			case 'C':
				return alternate ? "\\u" + Integer.toHexString((int) value & 0xFFFF).toUpperCase() : pad(String.valueOf(value));
			case 'd':
			case 'i':
			case 'u':
			case 'o':
			case 'x':
			case 'X':
			case 'b':
			case 'l':
			case 'L':
				return form((short) value);
			default:
				throw newIllegalArgumentException("cCdiuoxXblL");
		}
	}

	/**
	 * Formats a boolean into a string (like sprintf in C).
	 */
	public String form(boolean value) {
		if (fmt == 'l') {
			return pad(value ? "true" : "false");
		}
		if (fmt == 'L') {
			return pad(value ? "TRUE" : "FALSE");
		}
		throw newIllegalArgumentException("lL");

	}

	/**
	 * Formats a double into a string (like sprintf in C).
	 */
	public String form(double x) {
		String r;

		if (precision < 0) {
			precision = 6;
		}

		int s = 1;
		if (x < 0) {
			x = -x;
			s = -1;
		}
		if (fmt == 'f') {
			r = fixedFormat(x);
		} else if (fmt == 'e' || fmt == 'E' || fmt == 'g' || fmt == 'G') {
			r = expFormat(x);
		} else {
			throw newIllegalArgumentException("feEgG");
		}
		return pad(sign(s, r));
	}

	/**
	 * Formats a long integer into a string (like sprintf in C).
	 */
	public String form(long x) {
		String r;
		int s = 0;

		switch (fmt) {
			case 'c':
				return form((char) x);
			case 'd':
				if (x < 0) {
					r = Long.toString(x).substring(1);
					s = -1;
				} else {
					r = Long.toString(x);
					s = 1;
				}
				r = groupDigits(r, 3, ',');
				break;
			case 'i':
				int xx = (int) x;
				if (xx < 0) {
					r = Integer.toString(xx).substring(1);
					s = -1;
				} else {
					r = Integer.toString(xx);
					s = 1;
				}
				r = groupDigits(r, 3, ',');
				break;
			case 'u':
				if (x < 0) {
					long xl = x & 0x7FFFFFFFFFFFFFFFL;
					r = Long.toString(xl);
					BigInteger bi = new BigInteger(r);
					r = bi.add(bgInt).toString();
				} else {
					r = Long.toString(x);
				}
				r = groupDigits(r, 3, ',');
				s = 1;
				break;
			case 'o':
				r = Long.toOctalString(x);
				break;
			case 'x':
				r = Long.toHexString(x);
				r = groupDigits(r, 4, ' ');
				break;
			case 'X':
				r = Long.toHexString(x).toUpperCase();
				r = groupDigits(r, 4, ' ');
				break;
			case 'b':
			case 'B':
				r = Long.toBinaryString(x);
				r = groupDigits(r, 8, ' ');
				break;
			case 'l':
				r = (x == 0 ? "false" : "true");
				break;
			case 'L':
				r = (x == 0 ? "FALSE" : "TRUE");
				break;
			default:
				throw new IllegalArgumentException("cdiuoxXbBlL");
		}

		return pad(sign(s, r));
	}

	/**
	 * Formats an integer into a string (like sprintf in C).
	 */
	public String form(int x) {
		String r;
		int s = 0;

		switch (fmt) {
			case 'c':
				return form((char) x);
			case 'd':
			case 'i':
				if (x < 0) {
					r = Integer.toString(x).substring(1);
					s = -1;
				} else {
					r = Integer.toString(x);
					s = 1;
				}
				r = groupDigits(r, 3, ',');
				break;
			case 'u':
				long xl = x & 0x00000000FFFFFFFFL;
				r = Long.toString(xl);
				r = groupDigits(r, 3, ',');
				s = 1;
				break;
			case 'o':
				r = Integer.toOctalString(x);
				break;
			case 'x':
				r = Integer.toHexString(x);
				r = groupDigits(r, 4, ' ');
				break;
			case 'X':
				r = Integer.toHexString(x).toUpperCase();
				r = groupDigits(r, 4, ' ');
				break;
			case 'b':
			case 'B':
				r = Integer.toBinaryString(x);
				r = groupDigits(r, 8, ' ');
				break;
			case 'l':
				r = (x == 0 ? "false" : "true");
				break;
			case 'L':
				r = (x == 0 ? "FALSE" : "TRUE");
				break;
			default:
				throw newIllegalArgumentException("cdiuoxXbBlL");
		}
		return pad(sign(s, r));
	}

	/**
	 * Formats a byte into a string (like sprintf in C).
	 */
	public String form(byte b) {
		return formInt(b, 0xFF);
	}

	/**
	 * Formats a short into a string (like sprintf in C).
	 */
	public String form(short s) {
		return formInt(s, 0xFFFF);
	}

	/**
	 * Formatter for both <code>byte</code> and <code>short</code> values.
	 */
	private String formInt(int value, int unsignedMask) {
		String r;
		int s = 0;

		switch (fmt) {
			case 'c':
				return form((char) value);
			case 'd':
			case 'i':
				if (value < 0) {
					r = Integer.toString(value).substring(1);
					s = -1;
				} else {
					r = Integer.toString(value);
					s = 1;
				}
				r = groupDigits(r, 3, ',');
				break;
			case 'u':
				int xl = value & unsignedMask;
				r = Integer.toString(xl);
				r = groupDigits(r, 3, ',');
				s = 1;
				break;
			case 'o':
				r = Integer.toOctalString(value & unsignedMask);
				break;
			case 'x':
				r = Integer.toHexString(value & unsignedMask);
				r = groupDigits(r, 4, ' ');
				break;
			case 'X':
				r = Integer.toHexString(value & unsignedMask).toUpperCase();
				r = groupDigits(r, 4, ' ');
				break;
			case 'b':
			case 'B':
				r = Integer.toBinaryString(value & unsignedMask);
				r = groupDigits(r, 8, ' ');
				break;
			case 'l':
				r = (value == 0 ? "false" : "true");
				break;
			case 'L':
				r = (value == 0 ? "FALSE" : "TRUE");
				break;
			default:
				throw newIllegalArgumentException("cdiuoxXblL");
		}
		return pad(sign(s, r));
	}

	/**
	 * Formats a object into a string depending on format (like sprintf in C).
	 * If object is a numeric type and format is not one object formats (like 's' or 'p')
	 * it will be converted to primitive and formatted as such.
	 */
	public String form(Object object) {
		if (object == null) {
			return StringPool.NULL;
		}

		switch (fmt) {
			case 's' :
				String s = object.toString();
				if (precision >= 0 && precision < s.length()) {
					s = s.substring(0, precision);
				}
				return pad(s);
			case 'p' :
				return Integer.toString(System.identityHashCode(object));
		}

		// check for numeric type
		if (object instanceof Number) {
			Number number = (Number) object;
			if (object instanceof Integer) {
				return form(number.intValue());
			}
			if (object instanceof Long) {
				return form(number.longValue());
			}
			if (object instanceof Double) {
				return form(number.doubleValue());
			}
			if (object instanceof Float) {
				return form(number.floatValue());
			}
			if (object instanceof Byte) {
				return form(number.byteValue());
			}
			if (object instanceof Short) {
				return form(number.shortValue());
			}
			else {
				return form(number.intValue());
			}
		}
		else if (object instanceof Character) {
			return form(((Character) object).charValue());
		}
		else if (object instanceof Boolean) {
			return form(((Boolean)object).booleanValue());
		}

		// throw exception about invalid 'object'-formats
		throw newIllegalArgumentException("sp");
	}

	/**
	 * Creates <code>IllegalArgumentException</code> with message.
	 */
	protected IllegalArgumentException newIllegalArgumentException(String allowedFormats) {
		return new IllegalArgumentException("Invalid format: '" + fmt + "' is not one of '" + allowedFormats + "'");
	}

}