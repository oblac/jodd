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

package jodd.format;

/**
 * Printf.
 * @see jodd.format.PrintfFormat
 */
public class Printf {

	// ---------------------------------------------------------------- primitives

	/**
	 * @see jodd.format.PrintfFormat#form(byte)
	 */
	public static String str(String format, byte value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(char)
	 */
	public static String str(String format, char value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(short)
	 */
	public static String str(String format, short value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(int)
	 */
	public static String str(String format, int value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(long)
	 */
	public static String str(String format, long value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(double)
	 */
	public static String str(String format, float value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(double)
	 */
	public static String str(String format, double value) {
		return new PrintfFormat(format).form(value);
	}

	/**
	 * @see jodd.format.PrintfFormat#form(boolean)
	 */
	public static String str(String format, boolean value) {
		return new PrintfFormat(format).form(value);
	}

	// ---------------------------------------------------------------- objects

	public static String str(String format, String value) {
		return new PrintfFormat(format).form(value);
	}

	public static String str(String format, Object param) {
		return new PrintfFormat(format).form(param);
	}

	// ---------------------------------------------------------------- multiple objects

	public static String str(String format, Object... params) {
		PrintfFormat pf = new PrintfFormat();
		for (Object param : params) {
			pf.reinit(format);
			format = pf.form(param);
		}
		return format;
	}

}