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

/**
 * Hash engines.
 */
public interface HashEngine {

	/**
	 * Returns the {@link BCrypt} hash tool with given rounds number for salt generation.
	 */
	public static HashEngine bcrypt(final int rounds) {
		return bcrypt(BCrypt.gensalt(rounds));
	}

	/**
	 * Returns the {@link BCrypt} hash tool with given salt.
	 */
	public static HashEngine bcrypt(final String salt) {
		return new HashEngine() {
			@Override
			public String hash(final String input) {
				return BCrypt.hashpw(input, salt);
			}

			@Override
			public boolean check(final String input, final String hash) {
				return BCrypt.checkpw(input, hash);
			}
		};
	}

	/**
	 * Returns PBK2DF2 hash.
	 */
	public static HashEngine pbk2() {
		final PBKDF2Hash pbkdf2Hash = new PBKDF2Hash();

		return new HashEngine() {
			@Override
			public String hash(final String input) {
				return pbkdf2Hash.createHash(input);
			}

			@Override
			public boolean check(final String input, final String hash) {
				return pbkdf2Hash.validatePassword(input, hash);
			}
		};
	}

	/**
	 * Creates a hash from the input string.
	 */
	public String hash(String input);

	/**
	 * Validates the input string with given hash. Returns {@code true}
	 * if input matches the hash.
	 */
	public boolean check(String input, String hash);

}
