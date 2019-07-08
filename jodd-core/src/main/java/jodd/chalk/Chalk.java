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

package jodd.chalk;

import jodd.util.StringPool;

/**
 * Chalk allows you to color output going to console.
 * @see Chalk256
 */
public class Chalk<T extends Chalk<T>> {

	/**
	 * Global flag that disables all the chalks.
	 * Useful for windows platforms :)
	 */
	public static boolean enabled = true;

	protected static final String RESET = "0";

	protected static final String BOLD = "1";
	protected static final String UNBOLD = "22";	// 21 isn't widely supported and 22 does the same thing
	protected static final String DIM = "2";
	protected static final String UNDIM = "22";
	protected static final String ITALIC = "3";
	protected static final String UNITALIC = "23";
	protected static final String UNDERLINE = "4";
	protected static final String UNUNDERLINE = "24";
	protected static final String INVERSE = "7";
	protected static final String UNINVERSE = "27";
	protected static final String HIDDEN = "8";
	protected static final String UNHIDDEN = "28";
	protected static final String STRIKETHROUGH = "9";
	protected static final String UNSTRIKETHROUGH = "29";

	protected static final String COLOR_RESET = "39";
	protected static final String BLACK = "30";
	protected static final String RED = "31";
	protected static final String GREEN = "32";
	protected static final String YELLOW = "33";
	protected static final String BLUE = "34";
	protected static final String MAGENTA = "35";
	protected static final String CYAN = "36";
	protected static final String WHITE = "37";
	protected static final String GRAY = "90";

	protected static final String BGCOLOR_RESET = "49";
	protected static final String BGBLACK = "40";
	protected static final String BGRED = "41";
	protected static final String BGGREEN = "42";
	protected static final String BGYELLOW = "43";
	protected static final String BGBLUE = "44";
	protected static final String BGMAGENTA = "45";
	protected static final String BGCYAN = "46";
	protected static final String BGWHITE = "47";

	protected StringBuilder prefix;
	protected StringBuilder suffix;
	protected String text;

	/**
	 * Creates new chalk.
	 */
	public static Chalk chalk() {
		return new Chalk();
	}

	@SuppressWarnings("unchecked")
	protected T _this() {
		return (T) this;
	}

	// ---------------------------------------------------------------- style

	public T bold() {
		startSequence(BOLD);
		endSequence(UNBOLD);
		return _this();
	}

	public T italic() {
		startSequence(ITALIC);
		endSequence(UNITALIC);
		return _this();
	}

	public T dim() {
		startSequence(DIM);
		endSequence(UNDIM);
		return _this();
	}

	public T underline() {
		startSequence(UNDERLINE);
		endSequence(UNUNDERLINE);
		return _this();
	}

	public T inverse() {
		startSequence(INVERSE);
		endSequence(UNINVERSE);
		return _this();
	}

	public T hidden() {
		startSequence(HIDDEN);
		endSequence(UNHIDDEN);
		return _this();
	}
	public T strikeThrough() {
		startSequence(STRIKETHROUGH);
		endSequence(UNSTRIKETHROUGH);
		return _this();
	}

	// ---------------------------------------------------------------- colors

	public T black() {
		startSequence(BLACK);
		endSequence(COLOR_RESET);
		return _this();
	}
	public T red() {
		startSequence(RED);
		endSequence(COLOR_RESET);
		return _this();
	}
	public T green() {
		startSequence(GREEN);
		endSequence(COLOR_RESET);
		return _this();
	}
	public T yellow() {
		startSequence(YELLOW);
		endSequence(COLOR_RESET);
		return _this();
	}
	public T blue() {
		startSequence(BLUE);
		endSequence(COLOR_RESET);
		return _this();
	}
	public T magenta() {
		startSequence(MAGENTA);
		endSequence(COLOR_RESET);
		return _this();
	}
	public T cyan() {
		startSequence(CYAN);
		endSequence(COLOR_RESET);
		return _this();
	}
	public T white() {
		startSequence(WHITE);
		endSequence(COLOR_RESET);
		return _this();
	}
	public T gray() {
		startSequence(GRAY);
		endSequence(COLOR_RESET);
		return _this();
	}
	public T grey() {
		return gray();
	}

	// ---------------------------------------------------------------- bg colors

	public T bgBlack() {
		startSequence(BGBLACK);
		endSequence(BGCOLOR_RESET);
		return _this();
	}
	public T bgRed() {
		startSequence(BGRED);
		endSequence(BGCOLOR_RESET);
		return _this();
	}
	public T bgGreen() {
		startSequence(BGGREEN);
		endSequence(BGCOLOR_RESET);
		return _this();
	}
	public T bgYellow() {
		startSequence(BGYELLOW);
		endSequence(BGCOLOR_RESET);
		return _this();
	}
	public T bgBlue() {
		startSequence(BGBLUE);
		endSequence(BGCOLOR_RESET);
		return _this();
	}
	public T bgMagenta() {
		startSequence(BGMAGENTA);
		endSequence(BGCOLOR_RESET);
		return _this();
	}
	public T bgCyan() {
		startSequence(BGCYAN);
		endSequence(BGCOLOR_RESET);
		return _this();
	}
	public T bgWhite() {
		startSequence(BGWHITE);
		endSequence(BGCOLOR_RESET);
		return _this();
	}

	// ---------------------------------------------------------------- internal

	protected void startSequence(final String value) {
		if (prefix == null) {
			prefix = new StringBuilder();
			prefix.append("\u001B[");
		}
		else {
			prefix.append(StringPool.SEMICOLON);
		}

		prefix.append(value);
	}

	protected void endSequence(final String value) {
		if (suffix == null) {
			suffix = new StringBuilder();
			suffix
				.append("\u001B[")
				.append(value);
		}
		else {
			suffix.insert(2, value + StringPool.SEMICOLON);
		}
	}

	// ---------------------------------------------------------------- out

	/**
	 * Returns chalked string.
	 */
	public String on(final String string) {
		if (!enabled) {
			return string;
		}

		final StringBuilder sb = new StringBuilder();

		if (prefix != null) {
			sb.append(prefix).append("m");
		}

		sb.append(string);

		if (suffix != null) {
			sb.append(suffix).append("m");
		}

		return sb.toString();
	}

	/**
	 * Prints chalked string to system output.
	 */
	public void print(final String string) {
		System.out.print(on(string));
	}

	/**
	 * Prints chalked string to system output.
	 */
	public void println(final String string) {
		System.out.println(on(string));
	}

}
