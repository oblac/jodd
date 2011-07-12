// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

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
