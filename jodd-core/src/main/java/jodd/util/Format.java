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

package jodd.util;

/**
 * Various string formatting and conversions.
 */
public class Format {

	/**
	 * Puts the text to the left and pads with spaces until the size is reached.
	 */
	public static String alignLeftAndPad(final String text, final int size) {
		int textLength = text.length();
		if (textLength > size) {
			return text.substring(0, size);
		}

		final StringBuilder sb = new StringBuilder(size);
		sb.append(text);
		while (textLength++ < size) {
			sb.append(' ');
		}
		return sb.toString();
	}

	/**
	 * Puts the text to the right and pads it with spaces until the size is reached.
	 */
	public static String alignRightAndPad(final String text, final int size) {
		int textLength = text.length();
		if (textLength > size) {
			return text.substring(textLength - size, textLength);
		}

		final StringBuilder sb = new StringBuilder(size);
		while (textLength++ < size) {
			sb.append(' ');
		}
		sb.append(text);
		return sb.toString();
	}

	/**
	 * Formats byte size to human readable bytecount.
	 * https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java/3758880#3758880
	 */
	public static String humanReadableByteCount(final long bytes, final boolean useSi) {
		final int unit = useSi ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		final int exp = (int) (Math.log(bytes) / Math.log(unit));
		final String pre = (useSi ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (useSi ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	/**
	 * Converts object into pretty string. All arrays are iterated.
	 */
	public static String toPrettyString(final Object value) {
		if (value == null) {
			return StringPool.NULL;
		}

		final Class<?> type = value.getClass();

		if (type.isArray()) {
			final Class componentType = type.getComponentType();

			if (componentType.isPrimitive()) {
				final StringBuilder sb = new StringBuilder();
				sb.append('[');

				if (componentType == int.class) {
					sb.append(ArraysUtil.toString((int[]) value));
				}
				else if (componentType == long.class) {
					sb.append(ArraysUtil.toString((long[]) value));
				}
				else if (componentType == double.class) {
					sb.append(ArraysUtil.toString((double[]) value));
				}
				else if (componentType == float.class) {
					sb.append(ArraysUtil.toString((float[]) value));
				}
				else if (componentType == boolean.class) {
					sb.append(ArraysUtil.toString((boolean[]) value));
				}
				else if (componentType == short.class) {
					sb.append(ArraysUtil.toString((short[]) value));
				}
				else if (componentType == byte.class) {
					sb.append(ArraysUtil.toString((byte[]) value));
				} else {
					throw new IllegalArgumentException();
				}
				sb.append(']');
				return sb.toString();
			} else {
				final StringBuilder sb = new StringBuilder();
				sb.append('[');

				final Object[] array = (Object[]) value;
				for (int i = 0; i < array.length; i++) {
					if (i > 0) {
						sb.append(',');
					}
					sb.append(toPrettyString(array[i]));
				}
				sb.append(']');
				return sb.toString();
			}
		} else if (value instanceof Iterable) {
			final Iterable iterable = (Iterable) value;
			final StringBuilder sb = new StringBuilder();
			sb.append('{');
			int i = 0;
			for (final Object o : iterable) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append(toPrettyString(o));
				i++;
			}
			sb.append('}');
			return sb.toString();
		}

		return value.toString();
	}



	// ---------------------------------------------------------------- camel case

	/**
	 * Changes CamelCase string to lower case words separated by provided
	 * separator character. The following translations are applied:
	 * <ul>
	 *     <li>Every upper case letter in the CamelCase name is translated into
	 * two characters, a separator and the lower case equivalent of the target character,
	 * with three exceptions.
	 * 		<ol><li>For contiguous sequences of upper case letters, characters after the first
	 * character are replaced only by their lower case equivalent, and are not
	 * preceded by a separator (<code>theFOO</code> to <code>the_foo</code>).
	 *		<li>An upper case character in the first position of the CamelCase name
	 * is not preceded by a separator character, and is translated only to its
	 * lower case equivalent. (<code>Foo</code> to <code>foo</code> and not <code>_foo</code>)
	 * 		<li>An upper case character in the CamelCase name that is already preceded
	 * by a separator character is translated only to its lower case equivalent,
	 * and is not preceded by an additional separator. (<code>user_Name</code>
	 * to <code>user_name</code> and not <code>user__name</code>.
	 * 		</ol>
	 * <li>If the CamelCase name starts with a separator, then that
	 * separator is not included in the translated name, unless the CamelCase
	 * name is just one character in length, i.e., it is the separator character.
	 * This applies only to the first character of the CamelCase name.
	 * </ul>
	 */
	public static String fromCamelCase(final String input, final char separator) {
		final int length = input.length();
		final StringBuilder result = new StringBuilder(length * 2);
		int resultLength = 0;
		boolean prevTranslated = false;
		for (int i = 0; i < length; i++) {
			char c = input.charAt(i);
			if (i > 0 || c != separator) {// skip first starting separator
				if (Character.isUpperCase(c)) {
					if (!prevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != separator) {
						result.append(separator);
						resultLength++;
					}
					c = Character.toLowerCase(c);
					prevTranslated = true;
				} else {
					prevTranslated = false;
				}
				result.append(c);
				resultLength++;
			}
		}
		return resultLength > 0 ? result.toString() : input;
	}

	/**
	 * Converts separated string value to CamelCase.
	 */
	public static String toCamelCase(final String input, final boolean firstCharUppercase, final char separator) {
		final int length = input.length();
		final StringBuilder sb = new StringBuilder(length);
		boolean upperCase = firstCharUppercase;

		for (int i = 0; i < length; i++) {
			final char ch = input.charAt(i);
			if (ch == separator) {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(ch));
				upperCase = false;
			} else {
				sb.append(ch);
			}
		}
		return sb.toString();
	}


	// ---------------------------------------------------------------- text

	/**
	 * Formats provided string as paragraph.
	 */
	public static String formatParagraph(final String src, final int len, final boolean breakOnWhitespace) {
		StringBuilder str = new StringBuilder();
		int total = src.length();
		int from = 0;
		while (from < total) {
			int to = from + len;
			if (to >= total) {
				to = total;
			} else if (breakOnWhitespace) {
				int ndx = StringUtil.lastIndexOfWhitespace(src, to - 1, from);
				if (ndx != -1) {
					to = ndx + 1;
				}
			}
			int cutFrom = StringUtil.indexOfNonWhitespace(src, from, to);
			if (cutFrom != -1) {
				int cutTo = StringUtil.lastIndexOfNonWhitespace(src, to - 1, from) + 1;
				str.append(src, cutFrom, cutTo);
			}
			str.append('\n');
			from = to;
		}
		return str.toString();
	}

	/**
	 * Converts all tabs on a line to spaces according to the provided tab width.
	 * This is not a simple tab to spaces replacement, since the resulting
	 * indentation remains the same.
	 */
	public static String convertTabsToSpaces(final String line, final int tabWidth) {
		int tab_index, tab_size;
		int last_tab_index = 0;
		int added_chars = 0;

		if (tabWidth == 0) {
			return StringUtil.remove(line, '\t');
		}

		StringBuilder result = new StringBuilder();

		while ((tab_index = line.indexOf('\t', last_tab_index)) != -1) {
			tab_size = tabWidth - ((tab_index + added_chars) % tabWidth);
			if (tab_size == 0) {
				tab_size = tabWidth;
			}
			added_chars += tab_size - 1;
			result.append(line, last_tab_index, tab_index);
			result.append(StringUtil.repeat(' ', tab_size));
			last_tab_index = tab_index+1;
		}

		if (last_tab_index == 0) {
			return line;
		}

		result.append(line.substring(last_tab_index));
		return result.toString();
	}

	// ---------------------------------------------------------------- java escape

	/**
	 * Escapes a string using java rules.
	 */
	public static String escapeJava(final String string) {
		int strLen = string.length();
		StringBuilder sb = new StringBuilder(strLen);

		for (int i = 0; i < strLen; i++) {
			char c = string.charAt(i);
			switch (c) {
				case '\b' : sb.append("\\b"); break;
				case '\t' : sb.append("\\t"); break;
				case '\n' : sb.append("\\n"); break;
				case '\f' : sb.append("\\f"); break;
				case '\r' : sb.append("\\r"); break;
				case '\"' : sb.append("\\\""); break;
				case '\\' : sb.append("\\\\"); break;
				default:
					if ((c < 32) || (c > 127)) {
						String hex = Integer.toHexString(c);
						sb.append("\\u");
						for (int k = hex.length(); k < 4; k++) {
							sb.append('0');
						}
						sb.append(hex);
					} else {
						sb.append(c);
					}
			}
		}
		return sb.toString();
	}

	/**
	 * Unescapes a string using java rules.
	 */
	public static String unescapeJava(final String str) {
		char[] chars = str.toCharArray();

		StringBuilder sb = new StringBuilder(str.length());
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c != '\\') {
				sb.append(c);
				continue;
			}
			i++;
			c = chars[i];
			switch (c) {
				case 'b': sb.append('\b'); break;
				case 't': sb.append('\t'); break;
				case 'n': sb.append('\n'); break;
				case 'f': sb.append('\f'); break;
				case 'r': sb.append('\r'); break;
				case '"': sb.append('\"'); break;
				case '\\': sb.append('\\'); break;
				case 'u' :
					char hex = (char) Integer.parseInt(new String(chars, i + 1, 4), 16);
					sb.append(hex);
					i += 4;
					break;
				default:
					throw new IllegalArgumentException("Invalid escaping character: " + c);
			}
		}
		return sb.toString();
	}


}
