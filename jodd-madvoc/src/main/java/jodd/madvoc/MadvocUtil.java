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

package jodd.madvoc;

import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 * Few madvoc utilities.
 */
public class MadvocUtil {

	public static String[] splitPathToChunks(final String actionPath) {
		String path = StringUtil.cutSurrounding(actionPath, StringPool.SLASH);
		return StringUtil.splitc(path, '/');
	}

	/**
	 * Locates last dot after the last slash or just slash.
	 */
	public static int lastIndexOfSlashDot(final String str) {
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
	public static int lastIndexOfDotAfterSlash(final String str) {
		int slashNdx = str.lastIndexOf('/');
		slashNdx++;
		return StringUtil.lastIndexOf(str, '.', str.length(), slashNdx);
	}

	/**
	 * Locates first dot after the last slash.
	 */
	public static int indexOfDotAfterSlash(final String str) {
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
			if (CharUtil.isUppercaseAlpha(name.charAt(ndx))) {
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
	public static String[] splitActionPath(final String actionPath) {
		return StringUtil.splitc(actionPath.substring(1), '/');
	}
}
