// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.props;

import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

import java.util.ArrayList;

/**
 * {@link Props} parser.
 */
public class PropsParser implements Cloneable {

	protected static final String PROFILE_LEFT = "<";
	protected static final String PROFILE_RIGHT = ">";

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
	 * Don't include empty properties.
	 */
	protected boolean skipEmptyProps = true;


	protected final PropsData propsData;

	public PropsParser() {
		this.propsData = new PropsData();
	}

	public PropsParser(PropsData propsData) {
		this.propsData = propsData;
	}

	public PropsData getPropsData() {
		return propsData;
	}

	@Override
	public PropsParser clone() {
		PropsParser pp = new PropsParser(this.propsData.clone());

		pp.escapeNewLineValue = escapeNewLineValue;
		pp.valueTrimLeft = valueTrimLeft;
		pp.valueTrimRight = valueTrimRight;
		pp.ignorePrefixWhitespacesOnNewLine = ignorePrefixWhitespacesOnNewLine;
		pp.skipEmptyProps = skipEmptyProps;

		return pp;
	}

	/**
	 * Parsing states.
	 */
	protected enum ParseState {
		TEXT,
		ESCAPE,
		ESCAPE_NEWLINE,
		COMMENT
	}

	/**
	 * Loads properties.
	 */
	public void parse(String in) {
		ParseState state = ParseState.TEXT;
		boolean insideSection = false;
		String currentSection = null;
		String key = null;
		StringBuilder sb = new StringBuilder();

		int len = in.length();
		int ndx = 0;
		while (ndx < len) {
			char c = in.charAt(ndx);
			ndx++;

			if (state == ParseState.COMMENT) {			// comment, skip to the end of the line
				if (c == '\n') {
					state = ParseState.TEXT;
				} else {
					continue;
				}
			}

			if (state == ParseState.ESCAPE) {
				switch (c) {
					case '\r':
					case '\n':
						state = ParseState.ESCAPE_NEWLINE;	// if the EOL is \n or \r\n, escapes both chars
						break;
					case 'u':		// encode UTF character
						int value = 0;

						for (int i = 0; i < 4; i++) {
							char hexChar = in.charAt(ndx++);
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
						state = ParseState.TEXT;
						break;
					case 't':
						sb.append('\t');
						state = PropsParser.ParseState.TEXT;
						break;
					case 'n':
						sb.append('\n');
						state = PropsParser.ParseState.TEXT;
						break;
					case 'r':
						sb.append('\r');
						state = PropsParser.ParseState.TEXT;
						break;
					case 'f':
						sb.append('\f');
						state = PropsParser.ParseState.TEXT;
						break;
					default:
						sb.append(c);
						state = ParseState.TEXT;
				}
				continue;
			}

			switch (c) {
				case '[':			// start section
					sb.setLength(0);
					insideSection = true;
					break;

				case ']': 			// end section
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

				case '\\': // escape char, take the next char as is
					state = ParseState.ESCAPE;
					break;

				case '#':
				case ';':
					state = ParseState.COMMENT;
					break;

				case '=': // assignment operator
				case ':':
					if (key == null) {
						key = sb.toString().trim();
						sb.setLength(0);
					} else {
						sb.append(c);
					}
					break;

				case '\r':
				case '\n':
					if ((state == ParseState.ESCAPE_NEWLINE) && (c == '\n')) {
						sb.append(escapeNewLineValue);
						if (ignorePrefixWhitespacesOnNewLine == false) {
							state = ParseState.TEXT;
						}
					} else {
						add(currentSection, key, sb);
						sb.setLength(0);
						key = null;
					}
					break;

				case ' ':
				case '\t':
					if ((state == ParseState.ESCAPE_NEWLINE)) {
						break;
					}
				default:
					sb.append(c);
					state = ParseState.TEXT;
			}
		}
		if (key != null) {
			add(currentSection, key, sb);
		}
	}

	/**
	 * Adds accumulated value to key and current section.
	 */
	protected void add(String section, String key, StringBuilder value) {
		if (value.length() == 0 && skipEmptyProps) {
			return;
		}
		if (key == null) {
			return;			// ignore lines without : or =
		}
		
		if (section != null) {
			key = section + '.' + key;
		}
		String v = value.toString();
		if (valueTrimLeft && valueTrimRight) {
			v = v.trim();
		} else if (valueTrimLeft) {
			v = StringUtil.trimLeft(v);
		} else {
			v = StringUtil.trimRight(v);
		}

		add(key, v);
	}

	/**
	 * Adds key-value to properties and profiles.
	 */
	protected void add(String key, String value) {
		int ndx = key.indexOf(PROFILE_LEFT);
		if (ndx == -1) {
			propsData.putBaseProperty(key, value);
			return;
		}

		// extract profiles
		ArrayList<String> keyProfiles = new ArrayList<String>();
		while (true) {
			ndx = key.indexOf(PROFILE_LEFT);
			if (ndx == -1) {
				break;
			}

			int len = key.length();

			int ndx2 = key.indexOf(PROFILE_RIGHT, ndx + 1);
			if (ndx2 == -1) {
				ndx2 = len;
			}

			// remember profile
			String profile = key.substring(ndx + 1, ndx2);
			keyProfiles.add(profile);

			// extract profile from key
			ndx2++;
			String right = (ndx2 == len) ? StringPool.EMPTY : key.substring(ndx2);
			key = key.substring(0, ndx) + right;
		}

		// add value to extracted profiles
		for (String p : keyProfiles) {
			propsData.putProfileProperty(key, value, p);
		}
	}



}