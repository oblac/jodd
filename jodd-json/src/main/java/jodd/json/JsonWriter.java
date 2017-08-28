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

package jodd.json;

import jodd.util.CharUtil;
import jodd.util.StringPool;

import java.io.IOException;

import static jodd.util.StringPool.NULL;

/**
 * Simple JSON writer.
 */
public class JsonWriter {

	protected final Appendable out;
	protected final boolean strictStringEncoding;

	public JsonWriter(Appendable out, boolean strictStringEncoding) {
		this.out = out;
		this.strictStringEncoding = strictStringEncoding;
	}

	// ---------------------------------------------------------------- push

	protected String pushedName;
	protected boolean pushedComma;
	protected boolean isPushed;

	/**
	 * Stores name to temporary stack. Used when name's value may or may not be
	 * serialized (e.g. it may be excluded), in that case we do not need to
	 * write the name.
	 */
	public void pushName(String name, boolean withComma) {
		pushedName = name;
		pushedComma = withComma;
		isPushed = true;
	}

	/**
	 * Writes stored name to JSON string. Cleans storage.
	 */
	protected void popName() {
		if (isPushed) {
			if (pushedComma) {
				writeComma();
			}
			String name = pushedName;
			pushedName = null;
			isPushed = false;
			writeName(name);
		}
	}

	/**
	 * Returns <code>true</code> if {@link #pushName(String, boolean)}  pushed name}
	 * has been {@link #popName() poped, i.e. used}.
	 */
	public boolean isNamePopped() {
		boolean b = !isPushed;
		isPushed = false;
		return b;
	}

	// ---------------------------------------------------------------- write

	/**
	 * Writes open object sign.
	 */
	public void writeOpenObject() {
		popName();
		write('{');
	}

	/**
	 * Writes close object sign.
	 */
	public void writeCloseObject() {
		write('}');
	}

	/**
	 * Writes object's property name: string and a colon.
	 */
	public void writeName(String name) {
		if (name != null) {
			writeString(name);
		}
		else {
			write(NULL);
		}

		write(':');
	}

	/**
	 * Writes open array sign.
	 */
	public void writeOpenArray() {
		popName();
		write('[');
	}

	/**
	 * Writes close array sign.
	 */
	public void writeCloseArray() {
		write(']');
	}

	/**
	 * Write a quoted and escaped value to the output.
	 */
	public void writeString(String value) {
		popName();

		write(StringPool.QUOTE);

		int len = value.length();

		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);

			switch (c) {
				case '"':
					write("\\\"");
					break;
				case '\\':
					write("\\\\");
					break;
				case '/':
					if (strictStringEncoding) {
						write("\\/");
					}
					else {
						write(c);
					}
					break;
				case '\b':
					write("\\b");
					break;
				case '\f':
					write("\\f");
					break;
				case '\n':
					write("\\n");
					break;
				case '\r':
					write("\\r");
					break;
				case '\t':
					write("\\t");
					break;
				default:
					if (Character.isISOControl(c)) {
						unicode(c);
					}
					else {
						write(c);
					}
			}
		}

		write(StringPool.QUOTE);
	}

	/**
	 * Writes unicode representation of a character.
	 */
	protected void unicode(char c) {
		write("\\u");
		int n = c;
		for (int i = 0; i < 4; ++i) {
			int digit = (n & 0xf000) >> 12;
			char hex = CharUtil.int2hex(digit);

			write(hex);

			n <<= 4;
		}
	}

	/**
	 * Writes comma.
	 */
	public void writeComma() {
		write(',');
	}

	/**
	 * Appends char sequence to the buffer. Used for numbers, nulls, booleans, etc.
	 */
	public void write(CharSequence charSequence) {
		popName();
		try {
			out.append(charSequence);
		} catch (IOException ioex) {
			throw new JsonException(ioex);
		}
	}

	public void writeNumber(Number number) {
		if (number == null) {
			write(StringPool.NULL);
			return;
		}
		write(number.toString());
	}

	/**
	 * Appends char to the buffer. Used internally.
	 */
	protected void write(char c) {
		try {
			out.append(c);
		} catch (IOException ioex) {
			throw new JsonException(ioex);
		}
	}

}