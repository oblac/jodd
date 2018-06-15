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

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SipHash computes 64-bit message authentication code from a variable-length
 * message and 128-bit secret key. It was designed to be efficient even for short
 * inputs, with performance comparable to non-cryptographic hash functions, such
 * as CityHash[1], thus can be used in hash tables to prevent DoS collision attack
 * (hash flooding) or to authenticate network packets.
 * <p>
 * Functions in SipHash family are specified as SipHash-c-d, where c is the number
 * of rounds per message block and d is the number of finalization rounds. The
 * recommended parameters are SipHash-2-4 for best performance, and
 * SipHash-4-8 for conservative security
 */
public class SipHash {

	private static Random rnd = ThreadLocalRandom.current();

	public static long hashCode(final byte[] data) {
		long k0 = rnd.nextLong();
		long k1 = rnd.nextLong();

		return hash24(k0, k1, data);
	}

	public static long hashCode(final long k0, final long k1, final byte[] data) {
		return hash24(k0, k1, data);
	}

	private static long hash24(final long k0, final long k1, final byte[] data) {
		long v0 = 0x736f6d6570736575L ^ k0;
		long v1 = 0x646f72616e646f6dL ^ k1;
		long v2 = 0x6c7967656e657261L ^ k0;
		long v3 = 0x7465646279746573L ^ k1;
		long m;
		int last = data.length / 8 * 8;
		int i = 0;

		// processing 8 bytes blocks in data
		while (i < last) {
			// pack a block to long, as LE 8 bytes
			m = (long) data[i++]       |
				(long) data[i++] <<  8 |
				(long) data[i++] << 16 |
				(long) data[i++] << 24 |
				(long) data[i++] << 32 |
				(long) data[i++] << 40 |
				(long) data[i++] << 48 |
				(long) data[i++] << 56 ;
			// MSGROUND {
				v3 ^= m;
				// SIPROUND {
					v0 += v1;                    v2 += v3;
					v1 = (v1 << 13) | v1 >>> 51; v3 = (v3 << 16) | v3 >>> 48;
					v1 ^= v0;                    v3 ^= v2;
					v0 = (v0 << 32) | v0 >>> 32; v2 += v1;
					v0 += v3;                    v1 = (v1 << 17) | v1 >>> 47;
					v3 = (v3 << 21) | v3 >>> 43; v1 ^= v2;
					v3 ^= v0;                    v2 = (v2 << 32) | v2 >>> 32;
				// }
				// SIPROUND {
					v0 += v1;                    v2 += v3;
					v1 = (v1 << 13) | v1 >>> 51; v3 = (v3 << 16) | v3 >>> 48;
					v1 ^= v0;                    v3 ^= v2;
					v0 = (v0 << 32) | v0 >>> 32; v2 += v1;
					v0 += v3;                    v1 = (v1 << 17) | v1 >>> 47;
					v3 = (v3 << 21) | v3 >>> 43; v1 ^= v2;
					v3 ^= v0;                    v2 = (v2 << 32) | v2 >>> 32;
				// }
				v0 ^= m;
			// }
		}

		// packing the last block to long, as LE 0-7 bytes + the length in the top byte
		m = 0;
		for (i = data.length - 1; i >= last; --i) {
			m <<= 8; m |= (long) data[i];
		}
		m |= (long) data.length << 56;
		// MSGROUND {
			v3 ^= m;
			// SIPROUND {
				v0 += v1;                    v2 += v3;
				v1 = (v1 << 13) | v1 >>> 51; v3 = (v3 << 16) | v3 >>> 48;
				v1 ^= v0;                    v3 ^= v2;
				v0 = (v0 << 32) | v0 >>> 32; v2 += v1;
				v0 += v3;                    v1 = (v1 << 17) | v1 >>> 47;
				v3 = (v3 << 21) | v3 >>> 43; v1 ^= v2;
				v3 ^= v0;                    v2 = (v2 << 32) | v2 >>> 32;
			// }
			// SIPROUND {
				v0 += v1;                    v2 += v3;
				v1 = (v1 << 13) | v1 >>> 51; v3 = (v3 << 16) | v3 >>> 48;
				v1 ^= v0;                    v3 ^= v2;
				v0 = (v0 << 32) | v0 >>> 32; v2 += v1;
				v0 += v3;                    v1 = (v1 << 17) | v1 >>> 47;
				v3 = (v3 << 21) | v3 >>> 43; v1 ^= v2;
				v3 ^= v0;                    v2 = (v2 << 32) | v2 >>> 32;
			// }
			v0 ^= m;
		// }

		// finishing...
		v2 ^= 0xff;
		// SIPROUND {
			v0 += v1;                    v2 += v3;
			v1 = (v1 << 13) | v1 >>> 51; v3 = (v3 << 16) | v3 >>> 48;
			v1 ^= v0;                    v3 ^= v2;
			v0 = (v0 << 32) | v0 >>> 32; v2 += v1;
			v0 += v3;                    v1 = (v1 << 17) | v1 >>> 47;
			v3 = (v3 << 21) | v3 >>> 43; v1 ^= v2;
			v3 ^= v0;                    v2 = (v2 << 32) | v2 >>> 32;
		// }
		// SIPROUND {
			v0 += v1;                    v2 += v3;
			v1 = (v1 << 13) | v1 >>> 51; v3 = (v3 << 16) | v3 >>> 48;
			v1 ^= v0;                    v3 ^= v2;
			v0 = (v0 << 32) | v0 >>> 32; v2 += v1;
			v0 += v3;                    v1 = (v1 << 17) | v1 >>> 47;
			v3 = (v3 << 21) | v3 >>> 43; v1 ^= v2;
			v3 ^= v0;                    v2 = (v2 << 32) | v2 >>> 32;
		// }
		// SIPROUND {
			v0 += v1;                    v2 += v3;
			v1 = (v1 << 13) | v1 >>> 51; v3 = (v3 << 16) | v3 >>> 48;
			v1 ^= v0;                    v3 ^= v2;
			v0 = (v0 << 32) | v0 >>> 32; v2 += v1;
			v0 += v3;                    v1 = (v1 << 17) | v1 >>> 47;
			v3 = (v3 << 21) | v3 >>> 43; v1 ^= v2;
			v3 ^= v0;                    v2 = (v2 << 32) | v2 >>> 32;
		// }
		// SIPROUND {
			v0 += v1;                    v2 += v3;
			v1 = (v1 << 13) | v1 >>> 51; v3 = (v3 << 16) | v3 >>> 48;
			v1 ^= v0;                    v3 ^= v2;
			v0 = (v0 << 32) | v0 >>> 32; v2 += v1;
			v0 += v3;                    v1 = (v1 << 17) | v1 >>> 47;
			v3 = (v3 << 21) | v3 >>> 43; v1 ^= v2;
			v3 ^= v0;                    v2 = (v2 << 32) | v2 >>> 32;
		// }
		return v0 ^ v1 ^ v2 ^ v3;
	}
}
