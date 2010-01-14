// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

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

}
