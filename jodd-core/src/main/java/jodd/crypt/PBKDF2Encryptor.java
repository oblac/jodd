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

import jodd.util.Base64;
import jodd.util.StringUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import static jodd.util.StringPool.UTF_8;

/**
 * Symmetric de/encryptor that uses PBE With MD5 And Triple DES.
 * <b>Note: Requires Java8 u151</b> or installed <i>Unlimited Strength Jurisdiction Policy Files</i>.
 */
public class PBKDF2Encryptor {

	protected final Cipher ecipher;
	protected final Cipher dcipher;
	protected final int iterationCount;

	public PBKDF2Encryptor(final String passPhrase) {
		this(passPhrase, SecureRandom.getSeed(8), 65536, 256);
	}

	public PBKDF2Encryptor(final String passPhrase, final byte[] salt, final int iterationCount, final int i1) {
		this.iterationCount = iterationCount;
		try {
			// create the key
			KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount, i1);
			SecretKey tmp = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

			// encryptor
			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, secret);
			AlgorithmParameters params = ecipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

			// decryptor
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dcipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		}
		catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * Symmetrically encrypts the string.
	 */
	public String encrypt(final String str) {
		try {
			byte[] utf8 = StringUtil.getBytes(str);		// encode the string into bytes using utf-8
			byte[] enc = ecipher.doFinal(utf8); 	// encrypt
			return Base64.encodeToString(enc);		// encode bytes to base64 to get a string
		} catch (Throwable ignore) {
			return null;
		}
	}

	public byte[] encrypt(final byte[] input) {
		try {
			return ecipher.doFinal(input);
		} catch (Throwable ignore) {
			return null;
		}
	}

	/**
	 * Symmetrically decrypts the string.
	 */
	public String decrypt(String str) {
		try {
			str = StringUtil.replaceChar(str, ' ', '+');	// replace spaces with chars.
			byte[] dec = Base64.decode(str);    	// decode base64 to get bytes
			byte[] utf8 = dcipher.doFinal(dec);     // decrypt
			return new String(utf8, UTF_8);			// decode using utf-8
		} catch (Throwable ignore) {
			return null;
		}
	}

	public byte[] decrypt(final byte[] bytes) {
		try {
			return dcipher.doFinal(bytes);
		} catch (Throwable ignore) {
			return null;
		}
	}

}
