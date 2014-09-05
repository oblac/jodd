// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

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

	public JsonWriter(Appendable out) {
		this.out = out;
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
					write("\\/");
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