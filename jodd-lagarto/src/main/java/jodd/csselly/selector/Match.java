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

package jodd.csselly.selector;

import jodd.csselly.CSSellyException;
import jodd.util.StringUtil;

/**
 * {@link AttributeSelector Attribute} relation matcher.
 */
public enum Match {

	/**
	 * Represents an element with the att attribute whose value is exactly "val".
	 */
	EQUALS("=") {
		@Override
		public boolean compare(String attr, String val) {
			return val.equals(attr);
		}
	},

	/**
	 * Represents an element with the att attribute whose value is a whitespace-separated list of words,
	 * one of which is exactly "val". If "val" contains whitespace, it will never represent anything
	 * (since the words are separated by spaces). Also if "val" is the empty string, it will never represent anything.
	 */
	INCLUDES("~=") {
		@Override
		public boolean compare(String attr, String val) {
			if (val.length() == 0) {
				return false;
			}
			String[] attrarr = StringUtil.splitc(attr, ' ');
			for (String aa : attrarr) {
				if (aa.equals(val)) {
					return true;
				}
			}
			return false;
		}
	},

	/**
	 * Represents an element with the att attribute, its value either being exactly
	 * "val" or beginning with "val" immediately followed by "-"
	 */
	DASH("|=") {
		@Override
		public boolean compare(String attr, String val) {
			return attr.equals(val) || attr.startsWith(val + '-');
		}
	},

	/**
	 * Represents an element with the att attribute whose value begins with the prefix "val".
	 * If "val" is the empty string then the selector does not represent anything.
	 */
	PREFIX("^=") {
		@Override
		public boolean compare(String attr, String val) {
			if (val.length() == 0) {
				return false;
			}
			return attr.startsWith(val);
		}
	},

	/**
	 * Represents an element with the att attribute whose value ends with the suffix "val".
	 * If "val" is the empty string then the selector does not represent anything.
	 */
	SUFFIX("$=") {
		@Override
		public boolean compare(String attr, String val) {
			if (val.length() == 0) {
				return false;
			}
			return attr.endsWith(val);
		}
	},

	/**
	 * Represents an element with the att attribute whose value contains at least one instance of the substring "val".
	 * If "val" is the empty string then the selector does not represent anything.
	 */
	SUBSTRING("*=") {
		@Override
		public boolean compare(String attr, String val) {
			if (val.length() == 0) {
				return false;
			}
			return attr.contains(val);
		}
	};

	private final String sign;

	Match(String sign) {
		this.sign = sign;
	}

	/**
	 * Returns match sign.
	 */
	public String getSign() {
		return sign;
	}

	/**
	 * Compares attr and val values.
	 */
	public abstract boolean compare(String attr, String val);

	// ---------------------------------------------------------------- value of

	/**
	 * Resolves match type from the sign.
	 */
	public static Match valueOfSign(String sign) {
		Match[] values = Match.values();
		for (Match match : values) {
			if (match.getSign().equals(sign)) {
				return match;
			}
		}
		throw new CSSellyException("Invalid match sign: " + sign);
	}

	/**
	 * Resolves match type from the first character of the sign.
	 * It is assumed that the second character is '='.
	 */
	public static Match valueOfFirstChar(char firstChar) {
		Match[] values = Match.values();
		for (Match match : values) {
			String matchSign = match.getSign();
			if (matchSign.length() > 1) {
				if (firstChar == matchSign.charAt(0)) {
					return match;
				}
			}
		}
		return EQUALS;
	}
}
