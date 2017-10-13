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

package jodd.joy.crypt;

import jodd.util.Bits;
import jodd.util.StringPool;

import java.io.UnsupportedEncodingException;

/**
 * Threefish cipher.
 * http://www.schneier.com/threefish.html
 */
public class Threefish extends BlockCipher {

	// === GENERAL CONSTANTS ===

	private static final long EXTENDED_KEY_SCHEDULE_CONST = 6148914691236517205L;

	public static final int BLOCK_SIZE_BITS_256 = 256;
	public static final int BLOCK_SIZE_BITS_512 = 512;
	public static final int BLOCK_SIZE_BITS_1024 = 1024;

	private static final int ROUNDS_72 = 72;
	private static final int ROUNDS_80 = 80;

	private static final int WORDS_4 = 4;
	private static final int WORDS_8 = 8;
	private static final int WORDS_16 = 16;

	private static final int TWEAK_VALUES = 3;
	private static final int SUBKEY_INTERVAL = 4;

	// === VALUES FOR THE WORD PERMUTATION ===

	/**
	 * Word permutation constants for PI(i) for Nw = 4.
	 */
	private static final int[] PI4 = {0, 3, 2, 1};
	/**
	 * Word permutation constants for PI(i) for Nw = 8.
	 */
	private static final int[] PI8 = {2, 1, 4, 7, 6, 5, 0, 3};
	/**
	 * Word permutation constants for PI(i) for Nw = 16.
	 */
	private static final int[] PI16 = {0, 9, 2, 13, 6, 11, 4, 15, 10, 7, 12, 3, 14, 5, 8, 1};

	// === VALUES FOR THE REVERSE WORD PERMUTATION ===

	/**
	 * Reverse word permutation constants for PI(i) for Nw = 4.
	 */
	private static final int[] RPI4 = {0, 3, 2, 1}; // note: RPI4 == PI4
	/**
	 * Reverse word permutation constants for PI(i) for Nw = 8.
	 */
	private static final int[] RPI8 = {6, 1, 0, 7, 2, 5, 4, 3};
	/**
	 * Reverse word permutation constants for PI(i) for Nw = 16.
	 */
	private static final int[] RPI16 = {0, 15, 2, 11, 6, 13, 4, 9, 14, 1, 8, 5, 10, 3, 12, 7};

	// === ROTATION CONSTANTS FOR THE MIX FUNCTION ===

	private static final int DEPTH_OF_D_IN_R = 8;
	/**
	 * Rotational constants Rd,j for Nw = 4.
	 */
	private static final int[][] R4 = {
			{5, 56},
			{36, 28},
			{13, 46},
			{58, 44},
			{26, 20},
			{53, 35},
			{11, 42},
			{59, 50}
	};

	/**
	 * Rotational constants Rd,j for Nw = 8.
	 */
	private static final int[][] R8 = {
			{38, 30, 50, 53},
			{48, 20, 43, 31},
			{34, 14, 15, 27},
			{26, 12, 58, 7},
			{33, 49, 8, 42},
			{39, 27, 41, 14},
			{29, 26, 11, 9},
			{33, 51, 39, 35}
	};

	/**
	 * Rotation constants Rd,j for Nw = 16.
	 */
	private static final int[][] R16 = {
			{55, 43, 37, 40, 16, 22, 38, 12},
			{25, 25, 46, 13, 14, 13, 52, 57},
			{33, 8, 18, 57, 21, 12, 32, 54},
			{34, 43, 25, 60, 44, 9, 59, 34},
			{28, 7, 47, 48, 51, 9, 35, 41},
			{17, 6, 18, 25, 43, 42, 40, 15},
			{58, 7, 32, 45, 19, 18, 2, 56},
			{47, 49, 27, 58, 37, 48, 53, 56},
	};

	// === FIELDS CREATED DURING INSTANTIATION FOR PERFORMANCE REASONS ===

	private final long[] t = new long[TWEAK_VALUES]; // initial tweak words including t2
	private final long[] x = new long[2];
	private final long[] y = new long[2];

	// === FINAL FIELDS DETERMINED BY BLOCKSIZE ===

	private final int blockSize; // block size (in bits)
	private final int nr; // number of rounds depending on block size

	// === FIELDS DETERMINED BY KEY SIZE DURING INIT() ===

	private long[] k; // initial key words including knw
	private int nw; // number of key words excluding knw
	private int[] pi; // word permutation pi (depends on number of words <=> block size)
	private int[] rpi; // reverse word permutation rpi (depends on number of words <=> blocksize)
	private int[][] r; // rotational constants (depends on number of words <=> block size)

	// === FIELDS DETERMINED BY KEY SIZE DURING INIT() FOR PERFORMANCE REASONS ===

	// NOTE next fields use lazy instantiation
	// NOTE performance/memory: can we even use the same array? let's not before testing
	private long[] vd;
	private long[] ed;
	private long[] fd;
	private long[] ksd;

	/**
	 * Threefish implementation using the specified blocksize in bits.
	 *
	 * @param blockSize either 256, 512 or 1024 (bits)
	 */
	public Threefish(final int blockSize) {
		super(blockSize);
		this.blockSize = blockSize;

		switch (blockSize) {
			case BLOCK_SIZE_BITS_256:
			case BLOCK_SIZE_BITS_512:
				nr = ROUNDS_72;
				break;
			case BLOCK_SIZE_BITS_1024:
				nr = ROUNDS_80;
				break;
			default:
				throw new IllegalArgumentException("Illegal blocksize, use 256, 512 or 1024 bit values as blocksize");
		}
	}

	/**
	 * Threefish implementation using the specified blocksize in bits, specifying the number of rounds directly instead
	 * of using the default number of rounds depending on the blockSize. Mainly used for (performance) testing
	 * purposes.
	 *
	 * @param blockSize either 256, 512 or 1024 (bits)
	 * @param rounds the number of rounds 1..2^31, must be multiple of 4
	 */
	public Threefish(final int blockSize, final int rounds) {
		super(blockSize);
		this.blockSize = blockSize;

		switch (blockSize) {
			case BLOCK_SIZE_BITS_256:
			case BLOCK_SIZE_BITS_512:
			case BLOCK_SIZE_BITS_1024:
				break;
			default:
				throw new IllegalArgumentException("Illegal blocksize, use 256, 512 or 1024 bit values as blocksize");
		}

		if (rounds <= 0 || rounds % 4 != 0) {
			throw new IllegalArgumentException("Number of rounds should be at least 1 and should be a multiple of 4");
		}

		nr = rounds;
	}


	/**
	 * Initialize the cipher using the key and the tweak value.
	 *
	 * @param key the Threefish key to use
	 * @param tweak the tweak values to use
	 */
	public void init(final long[] key, final long[] tweak) {
		final int newNw = key.length;

		// only create new arrays if the value of N{w} changes (different key size)
		if (nw != newNw) {
			nw = newNw;

			switch (nw) {
				case WORDS_4:
					pi = PI4;
					rpi = RPI4;
					r = R4;
					break;
				case WORDS_8:
					pi = PI8;
					rpi = RPI8;
					r = R8;
					break;
				case WORDS_16:
					pi = PI16;
					rpi = RPI16;
					r = R16;
					break;
				default:
					throw new RuntimeException("Invalid threefish key");
			}

			this.k = new long[nw + 1];

			// instantiation of these fields here for performance reasons
			vd = new long[nw]; // v is the intermediate value v{d} at round d
			ed = new long[nw]; // ed is the value of e{d} at round d
			fd = new long[nw]; // fd is the value of f{d} at round d
			ksd = new long[nw]; // ksd is the value of k{s} at round d
		}

		System.arraycopy(key, 0, this.k, 0, key.length);

		long knw = EXTENDED_KEY_SCHEDULE_CONST;
		for (int i = 0; i < nw; i++) {
			knw ^= this.k[i];
		}
		this.k[nw] = knw;

		// set tweak values
		t[0] = tweak[0];
		t[1] = tweak[1];
		t[2] = t[0] ^ t[1];
	}

	/**
	 * Implementation of the E(K, T, P) function.
	 * The K and T values should be set previously using the init() method. This version is the 64 bit implementation
	 * of Threefish.
	 *
	 * @param p the initial plain text
	 * @param c the final value defined as value v{d} where d = N{r}
	 */
	public void blockEncrypt(final long[] p, final long[] c) {
		// initial value = plain
		System.arraycopy(p, 0, vd, 0, nw);

		for (int d = 0; d < nr; d++) { // do the rounds
			// calculate e{d,i}
			if (d % SUBKEY_INTERVAL == 0) {
				final int s = d / SUBKEY_INTERVAL;

				keySchedule(s);

				for (int i = 0; i < nw; i++) {
					ed[i] = vd[i] + ksd[i];
				}
			} else {
				System.arraycopy(vd, 0, ed, 0, nw);
			}

			for (int j = 0; j < nw / 2; j++) {
				x[0] = ed[j * 2];
				x[1] = ed[j * 2 + 1];

				mix(j, d);

				fd[j * 2] = y[0];
				fd[j * 2 + 1] = y[1];
			}

			for (int i = 0; i < nw; i++) {
				vd[i] = fd[pi[i]];
			}

		}

		// do the last keyschedule
		keySchedule(nr / SUBKEY_INTERVAL);

		for (int i = 0; i < nw; i++) {
			c[i] = vd[i] + ksd[i];
		}
	}

	/**
	 * Implementation of the MIX function.
	 *
	 * @param j the index in the rotation constants
	 * @param d the round
	 */
	private void mix(final int j, final int d) {
		y[0] = x[0] + x[1];
		final long rotl = r[d % DEPTH_OF_D_IN_R][j];
		// java left rotation for a long
		y[1] = (x[1] << rotl) | (x[1] >>> (Long.SIZE - rotl));
		y[1] ^= y[0];
	}


	/**
	 * Implementation of the D(K, T, C) function.
	 * The K and T values should be set previously using the init() method. This version is the 64 bit implementation
	 * of Threefish.
	 *
	 * @param c the cipher text
	 * @param p the plain text
	 */
	public void blockDecrypt(final long[] c, final long[] p) {
		// initial value = plain
		System.arraycopy(c, 0, vd, 0, nw);

		for (int d = nr; d > 0; d--) { // do the rounds
			// calculate e{d,i}
			if (d % SUBKEY_INTERVAL == 0) {
				final int s = d / SUBKEY_INTERVAL;
				keySchedule(s); // calculate same keys

				for (int i = 0; i < nw; i++) {
					fd[i] = vd[i] - ksd[i];
				}

			} else {
				System.arraycopy(vd, 0, fd, 0, nw);
			}

			for (int i = 0; i < nw; i++) {
				ed[i] = fd[rpi[i]];
			}

			for (int j = 0; j < nw / 2; j++) {
				y[0] = ed[j * 2];
				y[1] = ed[j * 2 + 1];

				demix(j, d - 1);

				vd[j * 2] = x[0];
				vd[j * 2 + 1] = x[1];
			}
		}

		// do the first keyschedule
		keySchedule(0);

		for (int i = 0; i < nw; i++) {
			p[i] = vd[i] - ksd[i];
		}
	}

	/**
	 * Implementation of the un-MIX function.
	 */
	private void demix(final int j, final int d) {
		y[1] ^= y[0];
		final long rotr = r[d % DEPTH_OF_D_IN_R][j]; // NOTE performance: darn, creation on stack!
		// right shift
		x[1] = (y[1] << (Long.SIZE - rotr)) | (y[1] >>> rotr);
		x[0] = y[0] - x[1];
	}


	/**
	 * Creates the subkeys.
	 *
	 * @param s the value of the round devided by 4
	 */
	private void keySchedule(final int s) {
		for (int i = 0; i < nw; i++) {
			// just put in the main key first
			ksd[i] = k[(s + i) % (nw + 1)];

			// don't add anything for i = 0,...,Nw - 4
			if (i == nw - 3) { // second to last
				ksd[i] += t[s % TWEAK_VALUES];
			} else if (i == nw - 2) { // first to last
				ksd[i] += t[(s + 1) % TWEAK_VALUES];
			} else if (i == nw - 1) { // last
				ksd[i] += s;
			}
		}
	}

	// ---------------------------------------------------------------- user friendly methods

	/**
	 * Initializes cipher in a simple way.
	 */
	public void init(String keyMessage, long tweak1, long tweak2) {
		long[] tweak = new long[] {tweak1, tweak2};
		byte[] key = new byte[blockSize / Byte.SIZE];
		byte[] keyData = getBytes(keyMessage);
		System.arraycopy(keyData, 0, key, 0, key.length < keyData.length ? key.length : keyData.length);
		init(bytesToLongs(key), tweak);
	}

	/**
	 * Encrypts a block.
	 */
	@Override
	public byte[] encryptBlock(byte[] content, int offset) {
		
		long[] contentBlock = bytesToLongs(content, offset, blockSizeInBytes);
		
		long[] encryptedBlock = new long[blockSize / Long.SIZE];

		blockEncrypt(contentBlock, encryptedBlock);

		return longsToBytes(encryptedBlock);
	}

	@Override
	public byte[] decryptBlock(byte[] encryptedContent, int offset) {
		long[] encryptedBlock = bytesToLongs(encryptedContent, offset, blockSizeInBytes);

		long[] decryptedBlock= new long[encryptedBlock.length];

		blockDecrypt(encryptedBlock, decryptedBlock);

		return longsToBytes(decryptedBlock);
	}

	/**
	 * Encrypts a string.
	 */
	public byte[] encryptString(String plain) {
		return encrypt(getBytes(plain));
	}

	/**
	 * Decrypts a string.
	 */
	public String decryptString(byte[] encrypted) {
		try {
			return new String(decrypt(encrypted), StringPool.UTF_8);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	// ---------------------------------------------------------------- util
	
	protected byte[] getBytes(String string) {
		try {
			return string.getBytes(StringPool.UTF_8);
		} catch (UnsupportedEncodingException ignore) {
			return null;
		}
	}

	protected static long[] bytesToLongs(byte[] ba) {
		return bytesToLongs(ba, 0, ba.length);
	}
	/**
	 * Converts segment of byte array into long array.
	 */
	protected static long[] bytesToLongs(byte[] ba, int offset, int size) {
		long[] result = new long[size >> 3];
		int i8 = offset;
		for (int i = 0; i < result.length; i++) {
			result[i] = Bits.getLong(ba, i8);
			i8 += 8;
		}
		return result;
	}

	protected static byte[] longsToBytes(long[] la) {
		byte[] result = new byte[la.length << 3];
		int i8 = 0;
		for (long l : la) {
			Bits.putLong(result, i8, l);
			i8 += 8;
		}
		return result;
	}


}
