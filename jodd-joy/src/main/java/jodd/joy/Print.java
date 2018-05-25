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

import jodd.util.Chalk256;
import jodd.util.StringUtil;

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

	public void out(final Chalk256 chalk256, final String string, final int maxLen) {
		System.out.print(chalk256.on(val(string, maxLen)));
	}
	public void outRight(final Chalk256 chalk256, final String string, final int maxLen) {
		System.out.print(chalk256.on(valRight(string, maxLen)));
	}

	private String val(final String value, final int len) {
		if (value.length() > len) {
			return value.substring(value.length() - len);
		}

		if (value.length() == len) {
			return value;
		}

		return value + StringUtil.repeat(' ', len - value.length());
	}

	private String valRight(final String value, final int len) {
		if (value.length() > len) {
			return value.substring(value.length() - len);
		}

		if (value.length() == len) {
			return value;
		}

		return StringUtil.repeat(' ', len - value.length()) + value;
	}

}
