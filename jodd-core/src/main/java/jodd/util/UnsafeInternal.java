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

import jodd.bridge.JavaIncompatible;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Simplified netty class.
 * Thanx: Stephane Landelle!
 *
 * @see https://github.com/netty/netty/blob/master/common/src/main/java/io/netty/util/internal/PlatformDependent.java
 */
@JavaIncompatible
final class UnsafeInternal {

	private static final Unsafe UNSAFE;
	private static final long STRING_VALUE_FIELD_OFFSET;
	private static final long STRING_OFFSET_FIELD_OFFSET;
	private static final long STRING_COUNT_FIELD_OFFSET;

	static {
		Unsafe unsafe = null;
		long stringValueFieldOffset = -1L;
		long stringOffsetFieldOffset = -1L;
		long stringCountFieldOffset = -1L;

		if (System.getProperty("java.version").startsWith("1.8")) {
			try {
				final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
				unsafeField.setAccessible(true);
				unsafe = (Unsafe) unsafeField.get(null);
			} catch (Throwable cause) {
				unsafe = null;
			}

			if (unsafe != null) {
				try {
					stringValueFieldOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
					stringOffsetFieldOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("offset"));
					stringCountFieldOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("count"));
				} catch (Throwable ignore) {
				}
			}
		}

		UNSAFE = unsafe;
		STRING_VALUE_FIELD_OFFSET = stringValueFieldOffset;
		STRING_OFFSET_FIELD_OFFSET = stringOffsetFieldOffset;
		STRING_COUNT_FIELD_OFFSET = stringCountFieldOffset;
	}

	static boolean hasUnsafe() {
		return UNSAFE != null;
	}

	static char[] unsafeGetChars(final String string) {
		final char[] value = (char[]) UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET);

		if (STRING_OFFSET_FIELD_OFFSET != -1) {
			// old String version with offset and count
			final int offset = UNSAFE.getInt(string, STRING_OFFSET_FIELD_OFFSET);
			final int count = UNSAFE.getInt(string, STRING_COUNT_FIELD_OFFSET);

			if (offset == 0 && count == value.length) {
				// no need to copy
				return value;

			} else {
				final char[] result = new char[count];
				System.arraycopy(value, offset, result, 0, count);
				return result;
			}

		} else {
			return value;
		}
	}

}