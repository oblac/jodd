// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

import jodd.util.CharUtil;
import jodd.util.StringUtil;

/**
 * Few madvoc utilities.
 */
public class MadvocUtil {

	/**
	 * Locates last dot after the last slash or just slash.
	 */
	public static int lastIndexOfSlashDot(String str) {
		int slashNdx = str.lastIndexOf('/');
		int dotNdx = StringUtil.lastIndexOf(str, '.', str.length(), slashNdx);
		if (dotNdx == -1) {
			if (slashNdx == -1) {
				return -1;
			}
			slashNdx++;
			if (slashNdx < str.length() - 1) {
				dotNdx = slashNdx;
			} else {
				dotNdx = -1;
			}
		}
		return dotNdx;
	}

	/**
	 * Locates last index of dot after the optional last slash.
	 */
	public static int lastIndexOfDotAfterSlash(String str) {
		int slashNdx = str.lastIndexOf('/');
		slashNdx++;
		return StringUtil.lastIndexOf(str, '.', str.length(), slashNdx);
	}

	/**
	 * Locates first dot after the last slash.
	 */
	public static int indexOfDotAfterSlash(String str) {
		int slashNdx = str.lastIndexOf('/');
		if (slashNdx == -1) {
			slashNdx = 0;
		}
		return str.indexOf('.', slashNdx);
	}


	/**
	 * Removes last CamelWord
	 */
	public static String stripLastCamelWord(String name) {

		int ndx = name.length() - 1;
		while (ndx >= 0) {
			if (CharUtil.isUppercaseLetter(name.charAt(ndx)) == true) {
				break;
			}
			ndx--;
		}
		if (ndx >= 0) {
			name = name.substring(0, ndx);
		}
		return name;
	}


	/**
	 * Splits action path to chunks.
	 */
	public static String[] splitActionPath(String actionPath) {
		return StringUtil.splitc(actionPath.substring(1), '/');
	}
}
