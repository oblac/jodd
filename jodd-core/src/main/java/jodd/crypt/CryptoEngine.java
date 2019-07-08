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
 * Symmetric encryption engines.
 */
public interface CryptoEngine {

	/**
	 * Creates new encryptor.
	 */
	public static CryptoEngine pbe3des(final String password) {
		final PBKDF2Encryptor PBKDF2Encryptor = new PBKDF2Encryptor(password);

		return new CryptoEngine() {
			@Override
			public byte[] encryptString(final String input) {
				return PBKDF2Encryptor.encrypt(StringUtil.getBytes(input));
			}

			@Override
			public String decryptString(final byte[] encryptedContent) {
				return StringUtil.newString(PBKDF2Encryptor.decrypt(encryptedContent));
			}
		};
	}

	/**
	 * Creates new {@link Threefish} encryptor.
	 */
	public static CryptoEngine threefish(String password) {
		final Threefish threefish = new Threefish(512);
		threefish.init(password, 0x1122334455667788L, 0xFF00FF00AABB9933L);

		return new CryptoEngine() {
			@Override
			public byte[] encryptString(final String input) {
				return threefish.encryptString(input);
			}

			@Override
			public String decryptString(final byte[] encryptedContent) {
				return threefish.decryptString(encryptedContent);
			}
		};
	}


	/**
	 * Encrypts the input string.
	 */
	public byte[] encryptString(String input);

	/**
	 * Decrypts the encrypted content to string.
	 */
	public String decryptString(byte[] encryptedContent);

}
