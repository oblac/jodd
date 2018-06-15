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

package jodd.crypt;

import jodd.util.StringUtil;

/**
 * MurmurHash3 128bits.
 * Adaptation of http://github.com/yonik/java_util for future updates to this file.
 */
public class MurmurHash3 {

	/**
	 * 128 bits of state.
	 */
	public static final class HashValue {
		public final long val1;
		public final long val2;

		public HashValue(final long val1, final long val2) {
			this.val1 = val1;
			this.val2 = val2;
		}

		@Override
		public String toString() {
			return Long.toHexString(val1) + Long.toHexString(val2);
		}
	}

	public static long fmix64(long k) {
		k ^= k >>> 33;
		k *= 0xff51afd7ed558ccdL;
		k ^= k >>> 33;
		k *= 0xc4ceb9fe1a85ec53L;
		k ^= k >>> 33;
		return k;
	}

	/**
	 * Gets a long from a byte buffer in little endian byte order.
	 */
	public static long getLongLittleEndian(final byte[] buf, final int offset) {
		return ((long) buf[offset + 7] << 56)   // no mask needed
			| ((buf[offset + 6] & 0xffL) << 48)
			| ((buf[offset + 5] & 0xffL) << 40)
			| ((buf[offset + 4] & 0xffL) << 32)
			| ((buf[offset + 3] & 0xffL) << 24)
			| ((buf[offset + 2] & 0xffL) << 16)
			| ((buf[offset + 1] & 0xffL) << 8)
			| ((buf[offset] & 0xffL));        // no shift needed
	}

	public static HashValue murmurhash3_x64_128(final String str, final int seed) {
		byte[] bytes = StringUtil.getBytes(str);
		return murmurhash3_x64_128(bytes, 0, bytes.length, seed);
	}

	/**
	 * Returns the MurmurHash3_x64_128 hash.
	 */
	public static HashValue murmurhash3_x64_128(final byte[] key, final int offset, final int len, final int seed) {
		// The original algorithm does have a 32 bit unsigned seed.
		// We have to mask to match the behavior of the unsigned types and prevent sign extension.
		long h1 = seed & 0x00000000FFFFFFFFL;
		long h2 = seed & 0x00000000FFFFFFFFL;

		final long c1 = 0x87c37b91114253d5L;
		final long c2 = 0x4cf5ad432745937fL;

		int roundedEnd = offset + (len & 0xFFFFFFF0);  // round down to 16 byte block
		for (int i = offset; i < roundedEnd; i += 16) {
			long k1 = getLongLittleEndian(key, i);
			long k2 = getLongLittleEndian(key, i + 8);
			k1 *= c1;
			k1 = Long.rotateLeft(k1, 31);
			k1 *= c2;
			h1 ^= k1;
			h1 = Long.rotateLeft(h1, 27);
			h1 += h2;
			h1 = h1 * 5 + 0x52dce729;
			k2 *= c2;
			k2 = Long.rotateLeft(k2, 33);
			k2 *= c1;
			h2 ^= k2;
			h2 = Long.rotateLeft(h2, 31);
			h2 += h1;
			h2 = h2 * 5 + 0x38495ab5;
		}

		long k1 = 0;
		long k2 = 0;

		switch (len & 15) {
			case 15:
				k2 = (key[roundedEnd + 14] & 0xffL) << 48;
			case 14:
				k2 |= (key[roundedEnd + 13] & 0xffL) << 40;
			case 13:
				k2 |= (key[roundedEnd + 12] & 0xffL) << 32;
			case 12:
				k2 |= (key[roundedEnd + 11] & 0xffL) << 24;
			case 11:
				k2 |= (key[roundedEnd + 10] & 0xffL) << 16;
			case 10:
				k2 |= (key[roundedEnd + 9] & 0xffL) << 8;
			case 9:
				k2 |= (key[roundedEnd + 8] & 0xffL);
				k2 *= c2;
				k2 = Long.rotateLeft(k2, 33);
				k2 *= c1;
				h2 ^= k2;
			case 8:
				k1 = ((long) key[roundedEnd + 7]) << 56;
			case 7:
				k1 |= (key[roundedEnd + 6] & 0xffL) << 48;
			case 6:
				k1 |= (key[roundedEnd + 5] & 0xffL) << 40;
			case 5:
				k1 |= (key[roundedEnd + 4] & 0xffL) << 32;
			case 4:
				k1 |= (key[roundedEnd + 3] & 0xffL) << 24;
			case 3:
				k1 |= (key[roundedEnd + 2] & 0xffL) << 16;
			case 2:
				k1 |= (key[roundedEnd + 1] & 0xffL) << 8;
			case 1:
				k1 |= (key[roundedEnd] & 0xffL);
				k1 *= c1;
				k1 = Long.rotateLeft(k1, 31);
				k1 *= c2;
				h1 ^= k1;
		}

		//----------
		// finalization

		h1 ^= len;
		h2 ^= len;

		h1 += h2;
		h2 += h1;

		h1 = fmix64(h1);
		h2 = fmix64(h2);

		h1 += h2;
		h2 += h1;

		return new HashValue(h1, h2);
	}

}