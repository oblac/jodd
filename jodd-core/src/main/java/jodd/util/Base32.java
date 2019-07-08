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

/**
 * Base32 encoding. Quite fast.
 */
public class Base32 {

	private static final String ERR_CANONICAL_LEN = "Invalid Base32 string length";
	private static final String ERR_CANONICAL_END = "Invalid end bits of Base32 string";
	private static final String ERR_INVALID_CHARS = "Invalid character in Base32 string";

	private static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();

	private static final byte[] LOOKUP = {
			26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1,			// 0123456789:;<=>?
			-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,			// @ABCDEFGHIJKLMNO
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, // PQRSTUVWXYZ[\]^_
			-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,			// `abcdefghijklmno
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25						// pqrstuvwxyz
	};


	/**
	 * Encode an array of binary bytes into a Base32 string.
	 */
	public static String encode(final byte[] bytes) {
		StringBuilder base32 = new StringBuilder((bytes.length * 8 + 4) / 5);

		int currByte, digit, i = 0;
		while (i < bytes.length) {

			// STEP 0; insert new 5 bits, leave 3 bits
			currByte = bytes[i++] & 255;
			base32.append(CHARS[currByte >> 3]);
			digit = (currByte & 7) << 2;
			if (i >= bytes.length) {
				base32.append(CHARS[digit]);
				break;
			}

			// STEP 3: insert 2 new bits, then 5 bits, leave 1 bit
			currByte = bytes[i++] & 255;
			base32.append(CHARS[digit | (currByte >> 6)]);
			base32.append(CHARS[(currByte >> 1) & 31]);
			digit = (currByte & 1) << 4;
			if (i >= bytes.length) {
				base32.append(CHARS[digit]);
				break;
			}

			// STEP 1: insert 4 new bits, leave 4 bit
			currByte = bytes[i++] & 255;
			base32.append(CHARS[digit | (currByte >> 4)]);
			digit = (currByte & 15) << 1;
			if (i >= bytes.length) {
				base32.append(CHARS[digit]);
				break;
			}

			// STEP 4: insert 1 new bit, then 5 bits, leave 2 bits
			currByte = bytes[i++] & 255;
			base32.append(CHARS[digit | (currByte >> 7)]);
			base32.append(CHARS[(currByte >> 2) & 31]);
			digit = (currByte & 3) << 3;
			if (i >= bytes.length) {
				base32.append(CHARS[digit]);
				break;
			}

			// STEP 2: insert 3 new bits, then 5 bits, leave 0 bit
			currByte = bytes[i++] & 255;
			base32.append(CHARS[digit | (currByte >> 5)]);
			base32.append(CHARS[currByte & 31]);
		}
		return base32.toString();
	}

	/**
	 * Decode a Base32 string into an array of binary bytes.
	 */
	public static byte[] decode(final String base32) throws IllegalArgumentException {
		switch (base32.length() % 8) {
			case 1:
			case 3:
			case 6:
				throw new IllegalArgumentException(ERR_CANONICAL_LEN);
		}

		byte[] bytes = new byte[base32.length() * 5 / 8];
		int offset = 0, i = 0, lookup;
		byte nextByte, digit;

		while (i < base32.length()) {
			lookup = base32.charAt(i++) - '2';
			if (lookup < 0 || lookup >= LOOKUP.length) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}
			digit = LOOKUP[lookup];
			if (digit == -1) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}

			// STEP n = 0: leave 5 bits
			nextByte = (byte) (digit << 3);
			lookup = base32.charAt(i++) - '2';
			if (lookup < 0 || lookup >= LOOKUP.length) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}
			digit = LOOKUP[lookup];
			if (digit == -1) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}

			// STEP n = 5: insert 3 bits, leave 2 bits
			bytes[offset++] = (byte) (nextByte | (digit >> 2));
			nextByte = (byte) ((digit & 3) << 6);
			if (i >= base32.length()) {
				if (nextByte != (byte) 0) {
					throw new IllegalArgumentException(ERR_CANONICAL_END);
				}
				break;
			}
			lookup = base32.charAt(i++) - '2';
			if (lookup < 0 || lookup >= LOOKUP.length) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}
			digit = LOOKUP[lookup];
			if (digit == -1) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}

			// STEP n = 2: leave 7 bits
			nextByte |= (byte) (digit << 1);
			lookup = base32.charAt(i++) - '2';
			if (lookup < 0 || lookup >= LOOKUP.length) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}
			digit = LOOKUP[lookup];
			if (digit == -1) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}

			// STEP n = 7: insert 1 bit, leave 4 bits
			bytes[offset++] = (byte) (nextByte | (digit >> 4));
			nextByte = (byte) ((digit & 15) << 4);
			if (i >= base32.length()) {
				if (nextByte != (byte) 0) {
					throw new IllegalArgumentException(ERR_CANONICAL_END);
				}
				break;
			}
			lookup = base32.charAt(i++) - '2';
			if (lookup < 0 || lookup >= LOOKUP.length) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}
			digit = LOOKUP[lookup];
			if (digit == -1) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}

			// STEP n = 4: insert 4 bits, leave 1 bit
			bytes[offset++] = (byte) (nextByte | (digit >> 1));
			nextByte = (byte) ((digit & 1) << 7);
			if (i >= base32.length()) {
				if (nextByte != (byte) 0) {
					throw new IllegalArgumentException(ERR_CANONICAL_END);
				}
				break;
			}
			lookup = base32.charAt(i++) - '2';
			if (lookup < 0 || lookup >= LOOKUP.length) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}
			digit = LOOKUP[lookup];
			if (digit == -1) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}

			// STEP n = 1: leave 6 bits
			nextByte |= (byte) (digit << 2);
			lookup = base32.charAt(i++) - '2';
			if (lookup < 0 || lookup >= LOOKUP.length) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}
			digit = LOOKUP[lookup];
			if (digit == -1) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}

			// STEP n = 6: insert 2 bits, leave 3 bits
			bytes[offset++] = (byte) (nextByte | (digit >> 3));
			nextByte = (byte) ((digit & 7) << 5);
			if (i >= base32.length()) {
				if (nextByte != (byte) 0) {
					throw new IllegalArgumentException(ERR_CANONICAL_END);
				}
				break;
			}
			lookup = base32.charAt(i++) - '2';
			if (lookup < 0 || lookup >= LOOKUP.length) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}
			digit = LOOKUP[lookup];
			if (digit == -1) {
				throw new IllegalArgumentException(ERR_INVALID_CHARS);
			}

			// STEP n = 3: insert 5 bits, leave 0 bit
			bytes[offset++] = (byte) (nextByte | digit);
		}
		return bytes;
	}

}
