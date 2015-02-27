// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

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