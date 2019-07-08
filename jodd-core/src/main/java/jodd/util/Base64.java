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

import java.util.Arrays;

/**
 * The <b>fastest</b> Base64 encoder/decoder implementations.
 * Base64 is defined in RFC 2045.
 * <p>
 * Encoding supports two modes - with or without line separator.
 * When line separator flag is on, result will have lines with
 * max size of 76 chars, as per spec.
 * <p>
 * When decoding, input must be valid, without illegal characters.
 * If input contains lines, they must be 76 chars long. Lines must
 * end with CRLF ("\r\n"), as per spec.
 */
public class Base64 {

	public static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
	private static final int[] INV = new int[256];

	static {
		Arrays.fill(INV, -1);
		for (int i = 0, iS = CHARS.length; i < iS; i++) {
			INV[CHARS[i]] = i;
		}
		INV['='] = 0;
	}

	// ---------------------------------------------------------------- char

	/**
	 * Encodes a raw byte array into a BASE64 <code>char[]</code>.
	 * @param lineSeparator optional CRLF after 76 chars, unless EOF.
	 */
	public static char[] encodeToChar(final byte[] arr, final boolean lineSeparator) {
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
	public static byte[] decode(final char[] arr) {
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

	public static byte[] encodeToByte(final String s) {
		return encodeToByte(StringUtil.getBytes(s), false);
	}

	public static byte[] encodeToByte(final String s, final boolean lineSep) {
		return encodeToByte(StringUtil.getBytes(s), lineSep);
	}

	public static byte[] encodeToByte(final byte[] arr) {
		return encodeToByte(arr, false);
	}

	/**
	 * Encodes a raw byte array into a BASE64 <code>char[]</code>.
	 * @param lineSep optional CRLF after 76 chars, unless EOF.
	 */
	public static byte[] encodeToByte(final byte[] arr, final boolean lineSep) {
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

	public static String decodeToString(final byte[] arr) {
		return StringUtil.newString(decode(arr));
	}

	/**
	 * Decodes BASE64 encoded byte array.
	 */
	public static byte[] decode(final byte[] arr) {
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

	public static String encodeToString(final String s) {
		return new String(encodeToChar(StringUtil.getBytes(s), false));
	}

	public static String encodeToString(final String s, final boolean lineSep) {
		return new String(encodeToChar(StringUtil.getBytes(s), lineSep));
	}

	public static String encodeToString(final byte[] arr) {
		return new String(encodeToChar(arr, false));
	}

	/**
	 * Encodes a raw byte array into a BASE64 <code>String</code>.
	 */
	public static String encodeToString(final byte[] arr, final boolean lineSep) {
		return new String(encodeToChar(arr, lineSep));
	}

	public static String decodeToString(final String s) {
		return StringUtil.newString(decode(s));
	}

	/**
	 * Decodes a BASE64 encoded string.
	 */
	public static byte[] decode(final String s) {
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