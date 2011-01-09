// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util.idgen;

import jodd.util.MathUtil;
import jodd.util.Base64;

/**
 * UUID generator of 24 bytes long values. Generated UUIDs are not sequential.
 */
public class Uuid24Generator {

	public static String generateUUID() {
		return new Uuid24Generator().generate();
	}

	/**
	 * Returns unique String of 16 chars. This string is based on following template:
	 * <ul>
	 * <li>8 bytes from current time,
	 * <li>8 bytes from identityHashCode
	 * <li>8 bytes from random
	 * </ul>
	 * Total 96 bits (24 bytes), that are coded with base 6 so resulting string will
	 * have just 16 chars.
	 */
	public String generate() {
		long id1 = System.currentTimeMillis() & 0xFFFFFFFFL;
		long id2 = System.identityHashCode(this);
		long id3 = MathUtil.randomLong(-0x80000000L, 0x80000000L) & 0xFFFFFFFFL;

		id1 <<= 16;
		id1 += (id2 & 0xFFFF0000L) >> 16;
		id3 += (id2 & 0x0000FFFFL) << 32;

		return unisgnedValueOf(id1) + unisgnedValueOf(id3);
	}

	private static final char[] chars64 = Base64.CHARS;
	
	private static String unisgnedValueOf(long l) {
		char[] buf = new char[64];
		int charNdx = 64;
		int radix = 1 << 6;
		long mask = radix - 1;
		do {
			buf[--charNdx] = chars64[(int)(l & mask)];
			l >>>= 6;
		} while (l != 0);
		return new String(buf, charNdx, (64 - charNdx));
	}

}
