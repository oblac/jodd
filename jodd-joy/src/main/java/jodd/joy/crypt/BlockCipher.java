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

import jodd.util.buffer.FastByteBuffer;

/**
 * Generic block chipher.
 */
public abstract class BlockCipher {
	
	protected static final byte TERMINATOR = -1;
	
	protected final int blockSizeInBytes;

	protected BlockCipher(int blockSize) {
		this.blockSizeInBytes = blockSize >> 3;
	}

	/**
	 * Encrypts single block of plain content. It ensures that there is
	 * always a block size amount of data.
	 */
	protected abstract byte[] encryptBlock(byte[] content, int offset);

	/**
	 * Decrypts single block of encrypted content.
	 */
	protected abstract byte[] decryptBlock(byte[] encryptedContent, int offset);

	/**
	 * Encrypts complete content, block by block.
	 */
	public byte[] encrypt(byte[] content) {
		FastByteBuffer fbb = new FastByteBuffer();

		int length = content.length + 1;
		int blockCount = length / blockSizeInBytes;
		int remaining = length;

		int offset = 0;
		for (int i = 0; i < blockCount; i++) {
			if (remaining == blockSizeInBytes) {
				break;
			}
			byte[] encrypted = encryptBlock(content, offset);

			fbb.append(encrypted);

			offset += blockSizeInBytes;
			remaining -= blockSizeInBytes;
		}

		if (remaining != 0) {
			// process remaining bytes
			byte[] block = new byte[blockSizeInBytes];

			System.arraycopy(content, offset, block, 0, remaining - 1);

			block[remaining - 1] = TERMINATOR;

			byte[] encrypted = encryptBlock(block, 0);

			fbb.append(encrypted);
		}

		return fbb.toArray();
	}

	/**
	 * Decrypts the whole content, block by block.
	 */
	public byte[] decrypt(byte[] encryptedContent) {
		FastByteBuffer fbb = new FastByteBuffer();

		int length = encryptedContent.length;
		int blockCount = length / blockSizeInBytes;

		int offset = 0;
		for (int i = 0; i < blockCount - 1; i++) {
			byte[] decrypted = decryptBlock(encryptedContent, offset);

			fbb.append(decrypted);

			offset += blockSizeInBytes;
		}

		// process last block
		byte[] decrypted = decryptBlock(encryptedContent, offset);

		// find terminator
		int ndx = blockSizeInBytes - 1;

		while (ndx >= 0) {
			if (decrypted[ndx] == TERMINATOR) {
				break;
			}
			ndx--;
		}

		fbb.append(decrypted, 0, ndx);

		return fbb.toArray();
	}
	
}
