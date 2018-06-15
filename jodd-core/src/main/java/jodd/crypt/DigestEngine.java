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

import jodd.io.StreamUtil;
import jodd.util.StringUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Digest engines.
 */
public interface DigestEngine {

	class JavaDigestEngine implements DigestEngine {

		private MessageDigest messageDigest;

		JavaDigestEngine(final String algorithm) {
			try {
				this.messageDigest = MessageDigest.getInstance(algorithm);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public byte[] digest(final byte[] byteArray) {
			messageDigest.update(byteArray);
			return messageDigest.digest();
		}

		@Override
		public byte[] digest(final File file) throws IOException {
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			DigestInputStream dis = null;

			try {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				dis = new DigestInputStream(bis, messageDigest);

				while (dis.read() != -1) {
				}
			}
			finally {
				StreamUtil.close(dis);
				StreamUtil.close(bis);
				StreamUtil.close(fis);
			}

			return messageDigest.digest();
		}
	}

	/**
	 * Creates new MD2 digest.
	 */
	public static DigestEngine md2() {
		return new JavaDigestEngine("MD2");
	}
	/**
	 * Creates new MD5 digest.
	 */
	public static DigestEngine md5() {
		return new JavaDigestEngine("MD5");
	}
	/**
	 * Creates new SHA-1 digest.
	 */
	public static DigestEngine sha1() {
		return new JavaDigestEngine("SHA-1");
	}
	/**
	 * Creates new SHA-256 digest.
	 */
	public static DigestEngine sha256() {
		return new JavaDigestEngine("SHA-256");
	}
	/**
	 * Creates new SHA-384 digest.
	 */
	public static DigestEngine sha384() {
		return new JavaDigestEngine("SHA-384");
	}
	/**
	 * Creates new SHA-512 digest.
	 */
	public static DigestEngine sha512() {
		return new JavaDigestEngine("SHA-512");
	}

	/**
	 * Returns byte-hash of input byte array.
	 */
	public byte[] digest(byte[] input);

	/**
	 * Returns byte-hash of input string.
	 */
	public default byte[] digest(final String input) {
		return digest(StringUtil.getBytes(input));
	}

	/**
	 * Returns digest of a file. Implementations may not read the whole
	 * file into the memory.
	 */
	public byte[] digest(final File file) throws IOException;

	/**
	 * Returns string hash of input byte array.
	 */
	public default String digestString(final byte[] byteArray) {
		return StringUtil.toHexString(digest(byteArray));
	}

	/**
	 * Returns string hash of input string.
	 */
	public default String digestString(final String input) {
		return StringUtil.toHexString(digest(input));
	}

	public default String digestString(final File file) throws IOException {
		return StringUtil.toHexString(digest(file));
	}

}