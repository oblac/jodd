// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.joy.auth;

/**
 * User AUTH data.
 */
public interface UserAuth {

	/**
	 * Returns users id.
	 */
	public long getUserId();

	/**
	 * Returns hashed password.
	 * Usually this is the value that is
	 * store in the database.
	 */
	public String getHashedPassword();

}