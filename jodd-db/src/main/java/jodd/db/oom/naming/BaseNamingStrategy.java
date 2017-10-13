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
	protected boolean strictAnnotationNames = true;

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

	public boolean isLowercase() {
		return !uppercase;
	}

	/**
	 * Alternative property to {@link #setUppercase(boolean)}.
	 * Does just the opposite.
	 */
	public void setLowercase(boolean lowercase) {
		this.uppercase = !lowercase;
	}

	public boolean isStrictAnnotationNames() {
		return strictAnnotationNames;
	}

	/**
	 * Defines if annotation names are strict, or if all the naming
	 * rules should apply on them, too.
	 */
	public void setStrictAnnotationNames(boolean strictAnnotationNames) {
		this.strictAnnotationNames = strictAnnotationNames;
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
