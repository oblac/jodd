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

/**
 * Simple {@code CharSequence} wrapper of the char array.
 * For {@code Appendable} version use {@link jodd.buffer.FastCharBuffer}.
 */
public class CharArraySequence implements CharSequence {

	public static final CharSequence EMPTY = CharArraySequence.of();

	private final char[] buffer;
	private final int off, len;

	/**
	 * Static constructor that creates a char sequence using provided char array.
	 */
	public static CharArraySequence of(final char... value) {
		return new CharArraySequence(value);
	}

	public static CharArraySequence of(final char[] value, final int offset, final int len) {
		return new CharArraySequence(value, offset, len);
	}

	/**
	 * Static constructor that creates a char sequence by making a copy of provided char array.
	 */
	public static CharArraySequence from(final char[] value, final int offset, final int len) {
		final char[] buffer = new char[value.length];

		System.arraycopy(value, offset, buffer, 0, len);

		return new CharArraySequence(buffer);
	}

	public CharArraySequence(final char[] value) {
		buffer = value;
		off = 0;
		len = value.length;
	}

	public CharArraySequence(final char[] value, final int offset, final int length) {
		if ((offset | length | offset + length | value.length - offset - length) < 0) {
			throw new IndexOutOfBoundsException();
		}
		buffer = value;
		off = offset;
		len = length;
	}

	/**
	 * Ctor without the bounds check.
	 */
	private CharArraySequence(final int offset, final int length, final char[] value) {
		off = offset;
		len = length;
		buffer = value;
	}

	@Override
	public int length() { return len; }

	@Override
	public String toString() { return String.valueOf(buffer, off, len); }

	@Override
	public char charAt(final int index) {
		//if ((index | len - index - 1) < 0) {
		if (index < 0) {
			throw new IndexOutOfBoundsException();
		}
		return buffer[off + index];
	}

	@Override
	public CharSequence subSequence(final int start, final int end) {
		final int count = end - start;
		final int rem = len - end;

		if ((start | end | count | rem) < 0) {
			throw new IndexOutOfBoundsException();
		}
		if ((start | rem) == 0) {
			return this;
		}

		return new CharArraySequence(off + start, count, buffer);
	}

}
