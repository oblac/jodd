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

package jodd.madvoc.macro;

import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Common class for <code>PathMacro</code> implementations.
 * Assume that macros are defined in the following way:
 * <code>prefix{name:pattern}suffix</code>.
 * Pattern is optional, and if missing all values are matched.
 */
public abstract class BasePathMacros implements PathMacros {

	protected int macrosCount;
	protected String[] names;		// macros names
	protected String[] patterns;	// macros patterns, if defined, elements may be null
	protected String[] fixed;		// array of fixed strings surrounding macros

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean init(final String actionPath, final String[] separators) {
		String prefix = separators[0];
		String split = separators[1];
		String suffix = separators[2];

		macrosCount = StringUtil.count(actionPath, prefix);

		if (macrosCount == 0) {
			return false;
		}

		names = new String[macrosCount];
		patterns = new String[macrosCount];
		fixed = new String[macrosCount + 1];

		int offset = 0;
		int i = 0;

		while (true) {
			int[] ndx = StringUtil.indexOfRegion(actionPath, prefix, suffix, offset);

			if (ndx == null) {
				break;
			}

			fixed[i] = actionPath.substring(offset, ndx[0]);

			String name = actionPath.substring(ndx[1], ndx[2]);

			// name:pattern
			String pattern = null;

			int colonNdx = name.indexOf(split);
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
	@Override
	public String[] names() {
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] patterns() {
		return patterns;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int macrosCount() {
		return macrosCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int match(final String actionPath) {
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
	@Override
	public String[] extract(final String actionPath) {
		return process(actionPath, false);
	}


	// ---------------------------------------------------------------- common

	/**
	 * Process action path in two modes: matching mode and extracting mode.
	 * @return string array of extracted macro values (null element is allowed) or null
	 */
	private String[] process(final String actionPath, final boolean match) {
		// first check the first fixed as a prefix
		if (match && !actionPath.startsWith(fixed[0])) {
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
				if (!matchValue(i, macroValue)) {
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