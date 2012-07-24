// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.oom.naming;

/**
 * Common stuff for both naming strategies.
 */
abstract class BaseNamingStrategy {

	// ---------------------------------------------------------------- common properties

	protected boolean splitCamelCase = true;
	protected char separatorChar = '_';
	protected boolean changeCase = true;
	protected boolean uppercase = true;

	public boolean isSplitCamelCase() {
		return splitCamelCase;
	}

	/**
	 * Specifies if camel case name has to be split.
	 * If set to <code>false</code>, then name is passed unchanged.
	 */
	public void setSplitCamelCase(boolean splitCamelCase) {
		this.splitCamelCase = splitCamelCase;
	}

	public char getSeparatorChar() {
		return separatorChar;
	}

	/**
	 * Separator character, when camel case names
	 * are {@link #setSplitCamelCase(boolean) split}.
	 */
	public void setSeparatorChar(char separatorChar) {
		this.separatorChar = separatorChar;
	}

	public boolean isChangeCase() {
		return changeCase;
	}

	/**
	 * Specifies if database names should be convert to
	 * uppercase or lowercase.
	 */
	public void setChangeCase(boolean changeCase) {
		this.changeCase = changeCase;
	}

	public boolean isUppercase() {
		return uppercase;
	}

	/**
	 * Specifies if table name should be converted to uppercase.
	 * Table names includes prefix and suffix. Otherwise, table name
	 * will be converted to lowercase.
	 */
	public void setUppercase(boolean uppercase) {
		this.uppercase = uppercase;
	}

	// ---------------------------------------------------------------- util methods

	protected static StringBuilder toUppercase(StringBuilder string) {
		int strLen = string.length();

		for (int i = 0; i < strLen; i++) {
			char c = string.charAt(i);

			char uppercaseChar = Character.toUpperCase(c);
			if (c != uppercaseChar) {
				string.setCharAt(i, uppercaseChar);
			}
		}
		return string;
	}

	protected static StringBuilder toLowercase(StringBuilder string) {
		int strLen = string.length();

		for (int i = 0; i < strLen; i++) {
			char c = string.charAt(i);

			char lowercaseChar = Character.toLowerCase(c);
			if (c != lowercaseChar) {
				string.setCharAt(i, lowercaseChar);
			}
		}
		return string;
	}

}
