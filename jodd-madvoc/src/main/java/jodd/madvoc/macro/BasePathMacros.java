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
public abstract class BasePathMacros implements PathMacros {

	protected static final char SPLIT = ':';

	protected int macrosCount;
	protected String[] names;		// macros names
	protected String[] patterns;	// macros patterns, if defined, elements may be null
	protected String[] fixed;		// array of fixed strings surrounding macros

	/**
	 * {@inheritDoc}
	 */
	public boolean init(final String actionPath) {
		macrosCount = StringUtil.count(actionPath, StringPool.DOLLAR_LEFT_BRACE);

		if (macrosCount == 0) {
			return false;
		}

		names = new String[macrosCount];
		patterns = new String[macrosCount];
		fixed = new String[macrosCount + 1];

		int offset = 0;
		int i = 0;

		while (true) {

			int[] ndx = StringUtil.indexOfRegion(actionPath, StringPool.DOLLAR_LEFT_BRACE, StringPool.RIGHT_BRACE, offset);

			if (ndx == null) {
				break;
			}

			fixed[i] = actionPath.substring(offset, ndx[0]);

			String name = actionPath.substring(ndx[1], ndx[2]);

			// name:pattern
			String pattern = null;

			int colonNdx = name.indexOf(SPLIT);
			if (colonNdx != -1) {
				pattern = name.substring(colonNdx + 1).trim();

				name = name.substring(0, colonNdx).trim();
			}

			this.patterns[i] = pattern;
			this.names[i] = name;

			// iterate
			offset = ndx[3];
			i++;
		}

		if (offset < actionPath.length()) {
			fixed[i] = actionPath.substring(offset);
		} else {
			fixed[i] = StringPool.EMPTY;
		}

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
	public String[] getPatterns() {
		return patterns;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMacrosCount() {
		return macrosCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public int match(String actionPath) {
		String[] values = process(actionPath, true);

		if (values == null) {
			return -1;
		}

		int macroChars = 0;
		for (String value : values) {
			if (value != null) {
				macroChars += value.length();
			}
		}

		return actionPath.length() - macroChars;
	}

	/**
	 * Matches the stripped value with the pattern.
	 * Pattern at provided <code>macroIndex</code> is not <code>null</code>!
	 */
	protected abstract boolean matchValue(int macroIndex, String value);

	/**
	 * {@inheritDoc}
	 */
	public String[] extract(String actionPath) {
		return process(actionPath, false);
	}


	// ---------------------------------------------------------------- common

	/**
	 * Process action path in two modes: matching mode and extracting mode.
	 * @return string array of extracted macro values (null element is allowed) or null
	 */
	private String[] process(String actionPath, boolean match) {
		// first check the first fixed as a prefix
		if (match && actionPath.startsWith(fixed[0]) == false) {
			return null;
		}

		String[] values = new String[macrosCount];

		int offset = fixed[0].length();
		int i = 0;

		while (i < macrosCount) {
			int nexti = i;

			// defines next fixed string to match
			String nextFixed;
			while (true) {
				nexti++;
				if (nexti > macrosCount) {
					nextFixed = null;	// match to the end of line
					break;
				}
				nextFixed = fixed[nexti];
				if (nextFixed.length() != 0) {
					break;
				}
				// next fixed is an empty string, so skip the next macro.
			}

			// find next fixed string
			int ndx;

			if (nextFixed != null) {
				ndx = actionPath.indexOf(nextFixed, offset);
			} else {
				ndx = actionPath.length();
			}

			if (ndx == -1) {
				return null;
			}

			String macroValue = actionPath.substring(offset, ndx);
			values[i] = macroValue;

			if (match && patterns[i] != null) {
				if (matchValue(i, macroValue) == false) {
					return null;
				}
			}

			if (nextFixed == null) {
				offset = ndx;
				break;
			}

			// iterate
			int nextFixedLength = nextFixed.length();
			offset = ndx + nextFixedLength;

			i = nexti;
		}

		if (offset != actionPath.length()) {
			// action path is not consumed fully during this matching
			return null;
		}

		return values;
	}

}