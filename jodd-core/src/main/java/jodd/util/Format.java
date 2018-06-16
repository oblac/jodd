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

public class Format {

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

	public static String alignRightAndPad(final String text, final int size) {
		int textLength = text.length();
		if (textLength > size) {
			return text.substring(size - textLength, textLength);
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

}
