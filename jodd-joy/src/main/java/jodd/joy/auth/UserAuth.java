// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.auth;

/**
 * User AUTH data.
 */
public interface UserAuth {

	/**
	 * Returns users ID used for authentication.
	 * Usually it is the same as users own ID.
	 */
	public long getUserAuthId();

	/**
	 * Returns hashed password.
	 * Usually this is the value that is
	 * stored in the database.
	 */
	public String getHashedPassword();

}