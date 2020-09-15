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

package jodd.joy;

import jodd.chalk.Chalk256;
import jodd.util.StringUtil;

/**
 * Print utility for printing values in columns.
 */
public class Print {

	public void line(final int width) {
		System.out.println(StringUtil.repeat('-', width));
	}

	public void line(final String name, final int width) {
		final int len = name.length() + 2;
		System.out.print(StringUtil.repeat('-', 1));
		System.out.print('[');
		System.out.print(Chalk256.chalk().bold().on(name));
		System.out.print(']');
		System.out.println(StringUtil.repeat('-', width - len - 1));
	}

	public void newLine() {
		System.out.println();
	}

	public void space() {
		System.out.print(' ');
	}

	public void space(int count) {
		while (count-- > 0) {
			System.out.print(' ');
		}
	}

	public void out(final Chalk256 chalk256, final String string) {
		System.out.print(chalk256.on(string));
	}
	public void out(final Chalk256 chalk256, final String string, final int maxLen) {
		System.out.print(chalk256.on(alignLeftAndPad(string, maxLen)));
	}

	public void outLeftRightNewLine(
			final Chalk256 chalk256Left,
			final String stringLeft,
			final Chalk256 chalk256Right,
			String stringRight,
			final int width) {

		final int leftLen = stringLeft.length();
		final int rightLen = stringRight.length();
		final int availWidth = width - 1;     // space delimiter

		int delta = leftLen + rightLen - availWidth;

		if (delta > 0) {
			// cut the right side
			if (stringRight.length() >= delta + 3) {
				stringRight = stringRight.substring(delta + 3);
				stringRight = "..." + stringRight;
			}
			else {
				stringRight = "";
			}
		}

		out(chalk256Left, stringLeft);
		space();
		while (delta++ < 0) {
			space();
		}
		out(chalk256Right, stringRight);
		newLine();
	}

	private static String alignLeftAndPad(final String text, final int size) {
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

}
