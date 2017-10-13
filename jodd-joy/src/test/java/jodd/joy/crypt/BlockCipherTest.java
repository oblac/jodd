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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlockCipherTest {

	static class FooBlockCipher extends BlockCipher {

		protected FooBlockCipher() {
			super(5 * 8);
		}

		@Override
		protected byte[] encryptBlock(byte[] content, int offset) {
			byte[] encrypted = new byte[blockSizeInBytes];
			System.arraycopy(content, offset, encrypted, 0, blockSizeInBytes);
			return encrypted;
		}

		@Override
		protected byte[] decryptBlock(byte[] encryptedContent, int offset) {
			byte[] decrypted = new byte[blockSizeInBytes];
			System.arraycopy(encryptedContent, offset, decrypted, 0, blockSizeInBytes);
			return decrypted;
		}
	}

	@Test
	public void testBlock8() {
		FooBlockCipher cypher = new FooBlockCipher();

		byte[] encrypted = cypher.encrypt("Jodd".getBytes());
		assertEquals("Jodd", new String(encrypted).substring(0, encrypted.length - 1));
		byte[] decrypted = cypher.decrypt(encrypted);
		assertEquals("Jodd", new String(decrypted));

		encrypted = cypher.encrypt("Jo".getBytes());
		assertEquals("Jo", new String(encrypted).substring(0, encrypted.length - 3));
		decrypted = cypher.decrypt(encrypted);
		assertEquals("Jo", new String(decrypted));

		encrypted = cypher.encrypt("Jodder".getBytes());
		assertEquals("Jodder", new String(encrypted).substring(0, encrypted.length - 4));
		decrypted = cypher.decrypt(encrypted);
		assertEquals("Jodder", new String(decrypted));
	}

}
