// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

/**
 * Text is a string divided in lines. 
 *
 * @see jodd.util.StringUtil
 */
public class TextUtil {

	/**
	 * Formats provided string as paragraph.
	 */
	public static String formatParagraph(String src, int len, boolean breakOnWs) {
		StringBuilder str = new StringBuilder();
		int total = src.length();
		int from = 0;
		while (from < total) {
			int to = from + len;
			if (to >= total) {
				to = total;
			} else if (breakOnWs) {
				int ndx = StringUtil.lastIndexOfWhitespace(src, to - 1, from);
				if (ndx != -1) {
					to = ndx + 1;
				}
			}
			int cutFrom = StringUtil.indexOfNonWhitespace(src, from, to);
			if (cutFrom != -1) {
				int cutTo = StringUtil.lastIndexOfNonWhitespace(src, to - 1, from) + 1;
				str.append(src.substring(cutFrom, cutTo));
			}
			str.append('\n');
			from = to;
		}
		return str.toString();
	}

	/**
	 * Converts all tabs on a line to spaces according to the provided tab width.
	 * Note that this methods works with <b>lines</b> only!
	 */
	public static String convertTabsToSpaces(String line, int tabWidth) {
		int tab_index, tab_size;
		int last_tab_index = 0;
		int added_chars = 0;
		StringBuilder result = new StringBuilder();
		while ((tab_index = line.indexOf('\t', last_tab_index)) != -1) {
			tab_size = tabWidth - ((tab_index + added_chars) % tabWidth);
			if (tab_size == 0) {
				tab_size = tabWidth;
			}
			added_chars += tab_size - 1;
			result.append(line.substring(last_tab_index, tab_index));
			result.append(StringUtil.repeat(' ', tab_size));
			last_tab_index = tab_index+1;
		}
		if (last_tab_index == 0) {
			return line;
		}
		result.append(line.substring(last_tab_index));
		return result.toString();
	}

}
