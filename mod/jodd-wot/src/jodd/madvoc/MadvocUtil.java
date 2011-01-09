// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc;

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
	 * Extracts http method from action path. Returns <code>null</code>
	 * if no http method is specified.
	 */
	public static String extractHttpMethodFromActionPath(String actionPath) {
		int hashNdx = actionPath.indexOf('#');
		if (hashNdx == -1) {
			return null;
		}
		return actionPath.substring(hashNdx + 1);
	}

	/**
	 * Strips http method from action path.
	 */
	public static String stripHttpMethodFromActionPath(String actionPath) {
		int hashNdx = actionPath.indexOf('#');
		if (hashNdx == -1) {
			return actionPath;
		}
		return actionPath.substring(0, hashNdx);
	}


}
