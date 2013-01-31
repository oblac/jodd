// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.macro;

import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Common class for <code>PathMacro</code> implementations.
 * Assume that macros are defined in the following way:
 * <code>prefix${name:pattern}suffix</code>.
 * Pattern is optional, and if missing all values are matched.
 */
public abstract class BasePathMacro implements PathMacro {

	protected final char SPLIT = ':';

	protected String prefix;
	protected String[] names;
	protected String pattern;
	protected String sufix;

	/**
	 * {@inheritDoc}
	 */
	public boolean init(final String chunk) {
		int[] ndx = StringUtil.indexOfRegion(chunk, StringPool.DOLLAR_LEFT_BRACE, StringPool.RIGHT_BRACE);

		if (ndx == null) {
			return false;
		}

		String name = chunk.substring(ndx[1], ndx[2]);

		int colonNdx = name.indexOf(SPLIT);

		String pattern = null;

		if (colonNdx != -1) {
			pattern = name.substring(colonNdx + 1).trim();

			name = name.substring(0, colonNdx).trim();
		}

		this.pattern = pattern;
		this.names = new String[]{name};
		this.prefix = (ndx[0] == 0 ? StringPool.EMPTY : chunk.substring(0, ndx[0]));
		this.sufix = (ndx[3] == chunk.length() ? StringPool.EMPTY : chunk.substring(ndx[3]));

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getNames() {
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	public int match(String chunk) {
		int matchedChars = 0;

		if (chunk.startsWith(prefix) == false) {
			return -1;
		}
		matchedChars += prefix.length();

		if (chunk.endsWith(sufix) == false) {
			return -1;
		}
		matchedChars += sufix.length();

		// match value
		if (pattern != null) {
			String value = chunk.substring(prefix.length(), chunk.length() - sufix.length());

			if (matchValue(value) == false) {
				return -1;
			}
		}

		// macro found
		return matchedChars;
	}

	/**
	 * Matches the stripped value with the pattern.
	 */
	protected abstract boolean matchValue(String value);

	/**
	 * {@inheritDoc}
	 */
	public String[] extract(String chunk) {
		int leftLen = prefix.length();
		int rightLen = sufix.length();

		if (leftLen + rightLen > 0) {
			// there is additional prefix and/or suffix
			chunk = chunk.substring(leftLen, chunk.length() - rightLen);
		}

		return new String[]{chunk};
	}
}
