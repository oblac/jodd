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

package jodd.joy.auth;

import jodd.joy.crypt.PasswordEncoder;
import jodd.petite.meta.PetiteInject;

/**
 * Abstract {@link UserAuth} manager as Petite bean.
 */
public abstract class UserAuthManagerBean<U extends UserAuth> {

	@PetiteInject
	protected PasswordEncoder passwordEncoder;

	/**
	 * Finds {@link UserAuth} object by
	 * provided username. Note that this can be any user information
	 * application is using, like e-mail, or any combination.
	 */
	public abstract U findUserAuthByUsername(String username);

	/**
	 * Finds {@link UserAuth} object by
	 * provided user id.
	 */
	public abstract U findUserAuthById(long id);

	/**
	 * Logins user. Usually updates some field of user, like last login time etc.
	 */
	public abstract void login(U userAuth);

	/**
	 * Checks users email and password by finding matching user.
	 */
	public final U findUser(String username, String rawPassword) {
		U userAuth = findUserAuthByUsername(username);
		if (userAuth == null) {
			return null;
		}
		if (passwordEncoder.isPasswordValid(userAuth.getHashedPassword(), rawPassword) == false) {
			return null;
		}
		return userAuth;
	}

	/**
	 * Finds an user for given userId and hashed password.
	 */
	public final U findUser(long userId, String hashedPassword) {
		U userAuth = findUserAuthById(userId);
		if (userAuth == null) {
			return null;
		}
		if (hashedPassword == null) {
			return null;
		}
		if (hashedPassword.equals(userAuth.getHashedPassword()) == false) {
			return null;
		}
		return userAuth;
	}

}