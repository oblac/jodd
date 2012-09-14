// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.JoddDefault;

import java.io.UnsupportedEncodingException;

/**
 * Various character and character sequence utilities, including <code>char[]</code> - <code>byte[]</code> conversions.
 */
public class CharUtil {

	// ---------------------------------------------------------------- simple

	/**
	 * Converts (signed) byte to (unsigned) char.
	 */
	public static char toChar(byte b) {
		return (char) (b & 0xFF);
	}

	/**
	 * Converts char array into byte array by stripping the high byte of each character.
	 */
	public static byte[] toSimpleByteArray(char[] carr) {
		byte[] barr = new byte[carr.length];
		for (int i = 0; i < carr.length; i++) {
			barr[i] = (byte) carr[i];
		}
		return barr;
	}

	/**
	 * Converts char sequence into byte array.
	 * @see #toSimpleByteArray(char[])
	 */
	public static byte[] toSimpleByteArray(CharSequence charSequence) {
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			barr[i] = (byte) charSequence.charAt(i);
		}
		return barr;
	}

	/**
	 * Converts byte array to char array by simply extending bytes to chars.
	 */
	public static char[] toSimpleCharArray(byte[] barr) {
		char[] carr = new char[barr.length];
		for (int i = 0; i < barr.length; i++) {
			carr[i] = (char) (barr[i] & 0xFF);
		}
		return carr;
	}

	// ---------------------------------------------------------------- ascii

	/**
	 * Returns ASCII value of a char. In case of overload, 0x3F is returned.
	 */
	public static int toAscii(char c) {
		if (c <= 0xFF) {
			return c;
		} else {
			return 0x3F;
		}
	}

	/**
	 * Converts char array into {@link #toAscii(char) ASCII} array.
	 */
	public static byte[] toAsciiByteArray(char[] carr) {
		byte[] barr = new byte[carr.length];
		for (int i = 0; i < carr.length; i++) {
			barr[i] = (byte) ((int) (carr[i] <= 0xFF ? carr[i] : 0x3F));
		}
		return barr;
	}

	/**
	 * Converts char sequence into ASCII byte array.
	 */
	public static byte[] toAsciiByteArray(CharSequence charSequence) {
		byte[] barr = new byte[charSequence.length()];
		for (int i = 0; i < barr.length; i++) {
			char c = charSequence.charAt(i);
			barr[i] = (byte) ((int) (c <= 0xFF ? c : 0x3F));
		}
		return barr;
	}

	// ---------------------------------------------------------------- raw arrays

	/**
	 * Converts char array into byte array by replacing each character with two bytes.
	 */
	public static byte[] toRawByteArray(char[] carr) {
		byte[] barr = new byte[carr.length << 1];
		for (int i = 0, bpos = 0; i < carr.length; i++) {
			char c = carr[i];
			barr[bpos++] = (byte) ((c & 0xFF00) >> 8);
			barr[bpos++] = (byte) (c & 0x00FF);
		}
		return barr;
	}

	public static char[] toRawCharArray(byte[] barr) {
		int carrLen = barr.length >> 1;
		if (carrLen << 1 < barr.length) {
			carrLen++;
		}
		char[] carr = new char[carrLen];
		int i = 0, j = 0;
		while (i < barr.length) {
			char c = (char) (barr[i] << 8);
			i++;

			if (i != barr.length) {
				c += barr[i] & 0xFF;
				i++;
			}
			carr[j++] = c;
		}
		return carr;
	}

	// ---------------------------------------------------------------- encoding

	/**
	 * Converts char array to byte array using default Jodd encoding.
	 */
	public static byte[] toByteArray(char[] carr) throws UnsupportedEncodingException {
		return new String(carr).getBytes(JoddDefault.encoding);
	}

	/**
	 * Converts char array to byte array using provided encoding.  
	 */
	public static byte[] toByteArray(char[] carr, String charset) throws UnsupportedEncodingException {
		return new String(carr).getBytes(charset);
	}

	/**
	 * Converts byte array of default Jodd encoding to char array.
	 */
	public static char[] toCharArray(byte[] barr) throws UnsupportedEncodingException {
		return new String(barr, JoddDefault.encoding).toCharArray();
	}

	/**
	 * Converts byte array of specific encoding to char array.
	 */
	public static char[] toCharArray(byte[] barr, String charset) throws UnsupportedEncodingException {
		return new String(barr, charset).toCharArray();
	}

	// ---------------------------------------------------------------- find


	/**
	 * Match if one character equals to any of the given character.
	 *
	 * @return <code>true</code> if characters match any character from given array,
	 *         otherwise <code>false</code>
	 */
	public static boolean equalsOne(char c, char[] match) {
		for (char aMatch : match) {
			if (c == aMatch) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds index of the first character in given array the matches any from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstEqual(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (equalsOne(source[i], match) == true) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the matches any from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstEqual(char[] source, int index, char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] == match) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * Finds index of the first character in given array the differs from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstDiff(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (equalsOne(source[i], match) == false) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the differs from the
	 * given set of characters.
	 *
	 * @return index of matched character or -1
	 */
	public static int findFirstDiff(char[] source, int index, char match) {
		for (int i = index; i < source.length; i++) {
			if (source[i] != match) {
				return i;
			}
		}
		return -1;
	}

	// ---------------------------------------------------------------- is

	/**
	 * Returns <code>true</code> if character is a white space (<= ' ').
	 * White space definition is taken from String class (see: <code>trim()</code>).
	 */
	public static boolean isWhitespace(char c) {
		return c <= ' ';
	}

	/**
	 * Returns <code>true</code> if specified character is lowercase ASCII.
	 * If user uses only ASCIIs, it is much much faster.
	 */
	public static boolean isLowercaseLetter(char c) {
		return (c >= 'a') && (c <= 'z');
	}

	/**
	 * Returns <code>true</code> if specified character is uppercase ASCII.
	 * If user uses only ASCIIs, it is much much faster.
	 */
	public static boolean isUppercaseLetter(char c) {
		return (c >= 'A') && (c <= 'Z');
	}

	public static boolean isLetter(char c) {
		return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
	}

	public static boolean isDigit(char c) {
		return (c >= '0') && (c <= '9');
	}

	public static boolean isLetterOrDigit(char c) {
		return isDigit(c) || isLetter(c);
	}

	public static boolean isWordChar(char c) {
		return isDigit(c) || isLetter(c) || (c == '_');
	}

	public static boolean isPropertyNameChar(char c) {
		return isDigit(c) || isLetter(c) || (c == '_') || (c == '.') || (c == '[') || (c == ']');
	}

	// ---------------------------------------------------------------- conversions

	/**
	 * Uppers lowercase ASCII char.
	 */
	public static char toUpperAscii(char c) {
		if (isLowercaseLetter(c)) {
			c -= (char) 0x20;
		}
		return c;
	}


	/**
	 * Lowers uppercase ASCII char.
	 */
	public static char toLowerAscii(char c) {
		if (isUppercaseLetter(c)) {
			c += (char) 0x20;
		}
		return c;
	}

}
