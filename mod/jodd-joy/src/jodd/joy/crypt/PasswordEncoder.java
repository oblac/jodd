package jodd.joy.crypt;

import jodd.petite.meta.PetiteBean;

/**
 * Encodes and validates passwords using {@link BCrypt}
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
		return encodePassword(rawPassword, null);
	}

	/**
	 * Encodes raw passwords. Result may be encoded password or just its hash code.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	public String encodePassword(String rawPassword, Object salt) {
		if (rawPassword == null) {
			return null;
		}
		return BCrypt.hashpw(rawPassword, BCrypt.gensalt(saltRounds));
	}

	/**
	 * Validates if provided password is equal to encoded password.
	 */
	@SuppressWarnings({"UnusedDeclaration"})
	public boolean isPasswordValid(String encodedPassword, String rawPassword, Object salt) {
		return BCrypt.checkpw(rawPassword, encodedPassword);
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
