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

package jodd.props;

import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Props} parser.
 */
public class PropsParser implements Cloneable {

	protected static final String PROFILE_LEFT = "<";

	protected static final String PROFILE_RIGHT = ">";

	protected final PropsData propsData;

	/**
	 * Value that will be inserted when escaping the new line.
	 */
	protected String escapeNewLineValue = StringPool.EMPTY;

	/**
	 * Trims left the value.
	 */
	protected boolean valueTrimLeft = true;

	/**
	 * Trims right the value.
	 */
	protected boolean valueTrimRight = true;

	/**
	 * Defines if starting whitespaces when value is split in the new line
	 * should be ignored or not.
	 */
	protected boolean ignorePrefixWhitespacesOnNewLine = true;

	/**
	 * Defines if multi-line values may be written using triple-quotes
	 * as in python.
	 */
	protected boolean multilineValues = true;

	/**
	 * Don't include empty properties.
	 */
	protected boolean skipEmptyProps = true;

	public PropsParser() {
		this.propsData = new PropsData();
	}

	public PropsParser(final PropsData propsData) {
		this.propsData = propsData;
	}

	public PropsData getPropsData() {
		return propsData;
	}

	@Override
	public PropsParser clone() {
		final PropsParser pp = new PropsParser(this.propsData.clone());

		pp.escapeNewLineValue = escapeNewLineValue;
		pp.valueTrimLeft = valueTrimLeft;
		pp.valueTrimRight = valueTrimRight;
		pp.ignorePrefixWhitespacesOnNewLine = ignorePrefixWhitespacesOnNewLine;
		pp.skipEmptyProps = skipEmptyProps;
		pp.multilineValues = multilineValues;

		return pp;
	}

	/**
	 * Parsing states.
	 */
	protected enum ParseState {
		TEXT,
		ESCAPE,
		ESCAPE_NEWLINE,
		COMMENT,
		VALUE
	}

	/**
	 * Different assignment operators.
	 */
	protected enum Operator {
		ASSIGN,
		QUICK_APPEND,
		COPY
	}

	/**
	 * Loads properties.
	 */
	public void parse(final String in) {
		ParseState state = ParseState.TEXT;
		ParseState stateOnEscape = null;

		boolean insideSection = false;
		String currentSection = null;
		String key = null;
		Operator operator = Operator.ASSIGN;
		final StringBuilder sb = new StringBuilder();

		final int len = in.length();
		int ndx = 0;
		while (ndx < len) {
			final char c = in.charAt(ndx);
			ndx++;

			if (state == ParseState.COMMENT) {
				// comment, skip to the end of the line
				if (c == '\r') {
					if ((ndx < len) && (in.charAt(ndx) == '\n')) {
						ndx++;
					}
					state = ParseState.TEXT;
				}
				else if (c == '\n') {
					state = ParseState.TEXT;
				}
			} else if (state == ParseState.ESCAPE) {
				state = stateOnEscape;  //ParseState.VALUE;
				switch (c) {
					case '\r':
						if ((ndx < len) && (in.charAt(ndx) == '\n')) {
							ndx++;
						}
					case '\n':
						// need to go 1 step back in order to escape
						// the current line ending in the follow-up state
						ndx--;
						state = ParseState.ESCAPE_NEWLINE;
						break;
					// encode UTF character
					case 'u':
						int value = 0;

						for (int i = 0; i < 4; i++) {
							final char hexChar = in.charAt(ndx++);
							if (CharUtil.isDigit(hexChar)) {
								value = (value << 4) + hexChar - '0';
							} else if (hexChar >= 'a' && hexChar <= 'f') {
								value = (value << 4) + 10 + hexChar - 'a';
							} else if (hexChar >= 'A' && hexChar <= 'F') {
								value = (value << 4) + 10 + hexChar - 'A';
							} else {
								throw new IllegalArgumentException("Malformed \\uXXXX encoding.");
							}
						}
						sb.append((char) value);
						break;
					case 't':
						sb.append('\t');
						break;
					case 'n':
						sb.append('\n');
						break;
					case 'r':
						sb.append('\r');
						break;
					case 'f':
						sb.append('\f');
						break;
					default:
						sb.append(c);
				}
			} else if (state == ParseState.TEXT) {
				switch (c) {
					case '\\':
						// escape char, take the next char as is
						stateOnEscape = state;
						state = ParseState.ESCAPE;
						break;

					// start section
					case '[':
						sb.setLength(0);
						insideSection = true;
						break;

					// end section
					case ']':
						if (insideSection) {
							currentSection = sb.toString().trim();
							sb.setLength(0);
							insideSection = false;
							if (currentSection.length() == 0) {
								currentSection = null;
							}
						} else {
							sb.append(c);
						}
						break;

					case '#':
					case ';':
						state = ParseState.COMMENT;
						break;

					// copy operator
					case '<':
						if (ndx == len || in.charAt(ndx) != '=') {
							sb.append(c);
							break;
						}
						operator = Operator.COPY;
						//ndx++;
						continue;

					// assignment operator
					case '+':
						if (ndx == len || in.charAt(ndx) != '=') {
							sb.append(c);
							break;
						}
						operator = Operator.QUICK_APPEND;
						//ndx++;
						continue;
					case '=':
					case ':':
						if (key == null) {
							key = sb.toString().trim();
							sb.setLength(0);
						} else {
							sb.append(c);
						}
						state = ParseState.VALUE;
						break;

					case '\r':
					case '\n':
						add(currentSection, key, sb, true, operator);
						sb.setLength(0);
						key = null;
						operator = Operator.ASSIGN;
						break;

					case ' ':
					case '\t':
						// ignore whitespaces
						break;
					default:
						sb.append(c);
				}
			} else {
				switch (c) {
					case '\\':
						// escape char, take the next char as is
						stateOnEscape = state;
						state = ParseState.ESCAPE;
						break;

					case '\r':
						if ((ndx < len) && (in.charAt(ndx) == '\n')) {
							ndx++;
						}
					case '\n':
						if (state == ParseState.ESCAPE_NEWLINE) {
							sb.append(escapeNewLineValue);
							if (!ignorePrefixWhitespacesOnNewLine) {
								state = ParseState.VALUE;
							}
						} else {
							add(currentSection, key, sb, true, operator);
							sb.setLength(0);
							key = null;
							operator = Operator.ASSIGN;

							// end of value, continue to text
							state = ParseState.TEXT;
						}
						break;

					case ' ':
					case '\t':
						if (state == ParseState.ESCAPE_NEWLINE) {
							break;
						}
					default:
						sb.append(c);
						state = ParseState.VALUE;

						if (multilineValues) {
							if (sb.length() == 3) {

								// check for ''' beginning
								if (sb.toString().equals("'''")) {
									sb.setLength(0);
									int endIndex = in.indexOf("'''", ndx);
									if (endIndex == -1) {
										endIndex = in.length();
									}
									sb.append(in, ndx, endIndex);

									// append
									add(currentSection, key, sb, false, operator);
									sb.setLength(0);
									key = null;
									operator = Operator.ASSIGN;

									// end of value, continue to text
									state = ParseState.TEXT;
									ndx = endIndex + 3;
								}
							}
						}
				}
			}
		}

		if (key != null) {
			add(currentSection, key, sb, true, operator);
		}
	}

	// ---------------------------------------------------------------- add

	/**
	 * Adds accumulated value to key and current section.
	 */
	protected void add(
			final String section, final String key,
			final StringBuilder value, final boolean trim, final Operator operator) {

		// ignore lines without : or =
		if (key == null) {
			return;
		}
		String fullKey = key;

		if (section != null) {
			if (fullKey.length() != 0) {
				fullKey = section + '.' + fullKey;
			} else {
				fullKey = section;
			}
		}
		String v = value.toString();

		if (trim) {
			if (valueTrimLeft && valueTrimRight) {
				v = v.trim();
			} else if (valueTrimLeft) {
				v = StringUtil.trimLeft(v);
			} else {
				v = StringUtil.trimRight(v);
			}
		}

		if (v.length() == 0 && skipEmptyProps) {
			return;
		}

		extractProfilesAndAdd(fullKey, v, operator);
	}

	/**
	 * Extracts profiles from the key name and adds key-value to them.
	 */
	protected void extractProfilesAndAdd(final String key, final String value, final Operator operator) {
		String fullKey = key;
		int ndx = fullKey.indexOf(PROFILE_LEFT);
		if (ndx == -1) {
			justAdd(fullKey, value, null, operator);
			return;
		}

		// extract profiles
		ArrayList<String> keyProfiles = new ArrayList<>();

		while (true) {
			ndx = fullKey.indexOf(PROFILE_LEFT);
			if (ndx == -1) {
				break;
			}

			final int len = fullKey.length();

			int ndx2 = fullKey.indexOf(PROFILE_RIGHT, ndx + 1);
			if (ndx2 == -1) {
				ndx2 = len;
			}

			// remember profile
			final String profile = fullKey.substring(ndx + 1, ndx2);
			keyProfiles.add(profile);

			// extract profile from key
			ndx2++;
			final String right = (ndx2 == len) ? StringPool.EMPTY : fullKey.substring(ndx2);
			fullKey = fullKey.substring(0, ndx) + right;
		}

		if (fullKey.startsWith(StringPool.DOT)) {
			// check for special case when only profile is defined in section
			fullKey = fullKey.substring(1);
		}

		// add value to extracted profiles
		justAdd(fullKey, value, keyProfiles, operator);
	}

	/**
	 * Core key-value addition.
	 */
	protected void justAdd(final String key, final String value, final ArrayList<String> keyProfiles, final Operator operator) {
		if (operator == Operator.COPY) {
			HashMap<String,Object> target = new HashMap<>();

			String[] profiles = null;
			if (keyProfiles != null) {
				profiles = keyProfiles.toArray(new String[keyProfiles.size()]);
			}

			String[] sources = StringUtil.splitc(value, ',');
			for (String source : sources) {
				source = source.trim();

				// try to extract profile for parsing

				String[] lookupProfiles = profiles;
				String lookupProfilesString = null;

				int leftIndex = source.indexOf('<');
				if (leftIndex != -1) {
					int rightIndex = source.indexOf('>');

					lookupProfilesString = source.substring(leftIndex + 1, rightIndex);
					source = source.substring(0, leftIndex).concat(source.substring(rightIndex + 1));

					lookupProfiles = StringUtil.splitc(lookupProfilesString, ',');

					StringUtil.trimAll(lookupProfiles);
				}

				String[] wildcards = new String[] {source + ".*"};

				propsData.extract(target, lookupProfiles, wildcards, null);

				for (Map.Entry<String, Object> entry : target.entrySet()) {
					String entryKey = entry.getKey();
					String suffix = entryKey.substring(source.length());

					String newKey = key + suffix;

					String newValue = "${" + entryKey;
					if (lookupProfilesString != null) {
						newValue += "<" + lookupProfilesString + ">";
					}
					newValue += "}";

					if (profiles == null) {
						propsData.putBaseProperty(newKey, newValue, false);
					} else {
						for (final String p : profiles) {
							propsData.putProfileProperty(newKey, newValue, p, false);
						}
					}
				}
			}
			return;
		}

		boolean append = operator == Operator.QUICK_APPEND;
		if (keyProfiles == null) {
			propsData.putBaseProperty(key, value, append);
			return;
		}
		for (final String p : keyProfiles) {
			propsData.putProfileProperty(key, value, p, append);
		}

	}

}