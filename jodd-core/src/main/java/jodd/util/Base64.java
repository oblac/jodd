// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.JoddDefault;

import java.util.Arrays;
import java.io.UnsupportedEncodingException;

/**
 * One of the <b>fastest</b> Base64 encoder/decoder implementations.
 * Base64 encoding is defined in RFC 2045.
 */
public class Base64 {

	private static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	private static final int[] INV = new int[256];

	static {
		Arrays.fill(INV, -1);
		for (int i = 0, iS = CHARS.length; i < iS; i++) {
			INV[CHARS[i]] = i;
		}
		INV['='] = 0;
	}

	/**
	 * Returns Base64 characters, a clone of used array.
	 */
	public static char[] getAlphabet() {
		return CHARS.clone();
	}

	// ---------------------------------------------------------------- char[]

	public static char[] encodeToChar(String s) {
		try {
			return encodeToChar(s.getBytes(JoddDefault.encoding), false);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	public static char[] encodeToChar(byte[] arr) {
		return encodeToChar(arr, false);
	}

	/**
	 * Encodes a raw byte array into a BASE64 <code>char[]</code>.
	 */
	public static char[] encodeToChar(byte[] arr, boolean lineSeparator) {
		int len = arr != null ? arr.length : 0;
		if (len == 0) {
			return new char[0];
		}

		int evenlen = (len / 3) * 3;
		int cnt = ((len - 1) / 3 + 1) << 2;
		int destLen = cnt + (lineSeparator ? (cnt - 1) / 76 << 1 : 0);
		char[] dest = new char[destLen];

		for (int s = 0, d = 0, cc = 0; s < evenlen;) {
			int i = (arr[s++] & 0xff) << 16 | (arr[s++] & 0xff) << 8 | (arr[s++] & 0xff);

			dest[d++] = CHARS[(i >>> 18) & 0x3f];
			dest[d++] = CHARS[(i >>> 12) & 0x3f];
			dest[d++] = CHARS[(i >>> 6) & 0x3f];
			dest[d++] = CHARS[i & 0x3f];

			if (lineSeparator && (++cc == 19) && (d < (destLen - 2))) {
				dest[d++] = '\r';
				dest[d++] = '\n';
				cc = 0;
			}
		}

		int left = len - evenlen; // 0 - 2.
		if (left > 0) {
			int i = ((arr[evenlen] & 0xff) << 10) | (left == 2 ? ((arr[len - 1] & 0xff) << 2) : 0);

			dest[destLen - 4] = CHARS[i >> 12];
			dest[destLen - 3] = CHARS[(i >>> 6) & 0x3f];
			dest[destLen - 2] = left == 2 ? CHARS[i & 0x3f] : '=';
			dest[destLen - 1] = '=';
		}
		return dest;
	}

	/**
	 * Decodes a BASE64 encoded char array.
	 */
	public byte[] decode(char[] arr) {
		int length = arr.length;
		if (length == 0) {
			return new byte[0];
		}

		int sndx = 0, endx = length - 1;
		int pad = arr[endx] == '=' ? (arr[endx - 1] == '=' ? 2 : 1) : 0;
		int cnt = endx - sndx + 1;
		int sepCnt = length > 76 ? (arr[76] == '\r' ? cnt / 78 : 0) << 1 : 0;
		int len = ((cnt - sepCnt) * 6 >> 3) - pad;
		byte[] dest = new byte[len];

		int d = 0;
		for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) {
			int i = INV[arr[sndx++]] << 18 | INV[arr[sndx++]] << 12 | INV[arr[sndx++]] << 6 | INV[arr[sndx++]];

			dest[d++] = (byte) (i >> 16);
			dest[d++] = (byte) (i >> 8);
			dest[d++] = (byte) i;

			if (sepCnt > 0 && ++cc == 19) {
				sndx += 2;
				cc = 0;
			}
		}

		if (d < len) {
			int i = 0;
			for (int j = 0; sndx <= endx - pad; j++) {
				i |= INV[arr[sndx++]] << (18 - j * 6);
			}
			for (int r = 16; d < len; r -= 8) {
				dest[d++] = (byte) (i >> r);
			}
		}

		return dest;
	}

	// ---------------------------------------------------------------- byte

	public static byte[] encodeToByte(String s) {
		try {
			return encodeToByte(s.getBytes(JoddDefault.encoding), false);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	public static byte[] encodeToByte(byte[] arr) {
		return encodeToByte(arr, false);
	}

	/** Encodes a raw byte array into a BASE64 <code>byte[]</code>.
	 */
	public static byte[] encodeToByte(byte[] arr, boolean lineSep) {
		int len = arr != null ? arr.length : 0;
		if (len == 0) {
			return new byte[0];
		}

		int evenlen = (len / 3) * 3;
		int cnt = ((len - 1) / 3 + 1) << 2;
		int destlen = cnt + (lineSep ? (cnt - 1) / 76 << 1 : 0);
		byte[] dest = new byte[destlen];

		for (int s = 0, d = 0, cc = 0; s < evenlen;) {
			int i = (arr[s++] & 0xff) << 16 | (arr[s++] & 0xff) << 8 | (arr[s++] & 0xff);

			dest[d++] = (byte) CHARS[(i >>> 18) & 0x3f];
			dest[d++] = (byte) CHARS[(i >>> 12) & 0x3f];
			dest[d++] = (byte) CHARS[(i >>> 6) & 0x3f];
			dest[d++] = (byte) CHARS[i & 0x3f];

			if (lineSep && ++cc == 19 && d < destlen - 2) {
				dest[d++] = '\r';
				dest[d++] = '\n';
				cc = 0;
			}
		}

		int left = len - evenlen;
		if (left > 0) {
			int i = ((arr[evenlen] & 0xff) << 10) | (left == 2 ? ((arr[len - 1] & 0xff) << 2) : 0);

			dest[destlen - 4] = (byte) CHARS[i >> 12];
			dest[destlen - 3] = (byte) CHARS[(i >>> 6) & 0x3f];
			dest[destlen - 2] = left == 2 ? (byte) CHARS[i & 0x3f] : (byte) '=';
			dest[destlen - 1] = '=';
		}
		return dest;
	}

	/**
	 * Decodes a BASE64 encoded byte array.
	 */
	public static byte[] decode(byte[] arr) {
		int length = arr.length;
		if (length == 0) {
			return new byte[0];
		}

		int sndx = 0, endx = length - 1;
		int pad = arr[endx] == '=' ? (arr[endx - 1] == '=' ? 2 : 1) : 0;
		int cnt = endx - sndx + 1;
		int sepCnt = length > 76 ? (arr[76] == '\r' ? cnt / 78 : 0) << 1 : 0;
		int len = ((cnt - sepCnt) * 6 >> 3) - pad;
		byte[] dest = new byte[len];

		int d = 0;
		for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) {
			int i = INV[arr[sndx++]] << 18 | INV[arr[sndx++]] << 12 | INV[arr[sndx++]] << 6 | INV[arr[sndx++]];

			dest[d++] = (byte) (i >> 16);
			dest[d++] = (byte) (i >> 8);
			dest[d++] = (byte) i;

			if (sepCnt > 0 && ++cc == 19) {
				sndx += 2;
				cc = 0;
			}
		}

		if (d < len) {
			int i = 0;
			for (int j = 0; sndx <= endx - pad; j++) {
				i |= INV[arr[sndx++]] << (18 - j * 6);
			}
			for (int r = 16; d < len; r -= 8) {
				dest[d++] = (byte) (i >> r);
			}
		}

		return dest;
	}

	// ---------------------------------------------------------------- string

	public static String encodeToString(String s) {
		try {
			return new String(encodeToChar(s.getBytes(JoddDefault.encoding), false));
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}
	public static String decodeToString(String s) {
		try {
			return new String(decode(s), JoddDefault.encoding);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}


	public static String encodeToString(byte[] arr) {
		return new String(encodeToChar(arr, false));
	}

	/**
	 * Encodes a raw byte array into a BASE64 <code>String</code>.
	 */
	public static String encodeToString(byte[] arr, boolean lineSep) {
		return new String(encodeToChar(arr, lineSep));
	}

	/**
	 * Decodes a BASE64 encoded string.
	 */
	public static byte[] decode(String s) {
		int length = s.length();
		if (length == 0) {
			return new byte[0];
		}

		int sndx = 0, endx = length - 1;
		int pad = s.charAt(endx) == '=' ? (s.charAt(endx - 1) == '=' ? 2 : 1) : 0;
		int cnt = endx - sndx + 1;
		int sepCnt = length > 76 ? (s.charAt(76) == '\r' ? cnt / 78 : 0) << 1 : 0;
		int len = ((cnt - sepCnt) * 6 >> 3) - pad;
		byte[] dest = new byte[len];

		int d = 0;
		for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) {
			int i = INV[s.charAt(sndx++)] << 18 | INV[s.charAt(sndx++)] << 12 | INV[s.charAt(sndx++)] << 6 | INV[s.charAt(sndx++)];

			dest[d++] = (byte) (i >> 16);
			dest[d++] = (byte) (i >> 8);
			dest[d++] = (byte) i;

			if (sepCnt > 0 && ++cc == 19) {
				sndx += 2;
				cc = 0;
			}
		}

		if (d < len) {
			int i = 0;
			for (int j = 0; sndx <= endx - pad; j++) {
				i |= INV[s.charAt(sndx++)] << (18 - j * 6);
			}
			for (int r = 16; d < len; r -= 8) {
				dest[d++] = (byte) (i >> r);
			}
		}

		return dest;
	}

}