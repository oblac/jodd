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

package jodd.servlet.wrapper;

import jodd.util.ArraysUtil;

/**
 * Extracts type and encoding from Content-Type header.
 */
public class ContentTypeHeaderResolver {

	private static final char[] TSPECIALS = " ;()[]<>:,=?@\"\\".toCharArray();

	private final String type;
	private final String encoding;

	public ContentTypeHeaderResolver(String fullContentType) {
		int charsetNdx = fullContentType.lastIndexOf("charset=");

		encoding = charsetNdx != -1 ? extractContentTypeValue(fullContentType, charsetNdx + 8) : null;

		type = extractContentTypeValue(fullContentType, 0);
	}

	private String extractContentTypeValue(String type, int startIndex) {

		// skip spaces
		while (startIndex < type.length() && type.charAt(startIndex) == ' ') {
			startIndex++;
		}

		if (startIndex >= type.length()) {
			return null;
		}

		int endIndex = startIndex;

		if (type.charAt(startIndex) == '"') {
			startIndex++;
			endIndex = type.indexOf('"', startIndex);
			if (endIndex == -1) {
				endIndex = type.length();
			}
		} else {
			while (endIndex < type.length() && (!ArraysUtil.contains(TSPECIALS, type.charAt(endIndex)))) {
				endIndex++;
			}
		}
		return type.substring(startIndex, endIndex);
	}

	/**
	 * Returns content mime type.
	 */
	public String getMimeType() {
		return type;
	}

	/**
	 * Returns content encoding.
	 */
	public String getEncoding() {
		return encoding;
	}
}