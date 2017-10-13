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

import jodd.petite.meta.PetiteBean;
import jodd.util.BCrypt;

/**
 * Encodes and validates passwords using {@link BCrypt}.
 */
@PetiteBean
public class PasswordEncoder {

	protected int saltRounds = 12;

	public int getSaltRounds() {
		return saltRounds;
	}

	public void setSaltRounds(int saltRounds) {
		this.saltRounds = saltRounds;
	}

	/**
	 * Encodes raw passwords using default salt.
	 */
	public String encodePassword(String rawPassword) {
		if (rawPassword == null) {
			return null;
		}
		return BCrypt.hashpw(rawPassword, BCrypt.gensalt(saltRounds));
	}

	/**
	 * Validates if provided password is equal to encoded password.
	 */
	public boolean isPasswordValid(String encodedPassword, String rawPassword) {
		return BCrypt.checkpw(rawPassword, encodedPassword);
	}

	/**
	 * Encodes passwords.
	 */
	public static void main(String[] args) {
		PasswordEncoder passwordEncoder = new PasswordEncoder();
		if (args.length >= 1) {
			System.out.println(passwordEncoder.encodePassword(args[0]));
		}
	}
}
