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

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Simplified netty class.
 * Thanx: Stephane Landelle!
 *
 * @see https://github.com/netty/netty/blob/master/common/src/main/java/io/netty/util/internal/PlatformDependent.java
 */
final class PlatformInternal {

	private static final Unsafe UNSAFE;

	private PlatformInternal() {
	}

	static {
		ByteBuffer direct = ByteBuffer.allocateDirect(1);
		Field addressField;
		try {
			addressField = Buffer.class.getDeclaredField("address");
			addressField.setAccessible(true);
			if (addressField.getLong(ByteBuffer.allocate(1)) != 0) {
				// A heap buffer must have 0 address.
				addressField = null;
			}
			else {
				if (addressField.getLong(direct) == 0) {
					// A direct buffer must have non-zero address.
					addressField = null;
				}
			}
		}
		catch (Throwable t) {
			// Failed to access the address field.
			addressField = null;
		}

		Unsafe unsafe;
		if (addressField != null) {
			try {
				Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
				unsafeField.setAccessible(true);
				unsafe = (Unsafe) unsafeField.get(null);
			}
			catch (Throwable cause) {
				// Unsafe.copyMemory(Object, long, Object, long, long) unavailable.
				unsafe = null;
			}
		}
		else {
			// If we cannot access the address of a direct buffer, there's no point of using unsafe.
			// Let's just pretend unsafe is unavailable for overall simplicity.
			unsafe = null;
		}

		UNSAFE = unsafe;
	}

	static boolean hasUnsafe() {
		return UNSAFE != null;
	}

	// ---------------------------------------------------------------- string

	private static final long STRING_VALUE_FIELD_OFFSET;
	private static final long STRING_OFFSET_FIELD_OFFSET;
	private static final long STRING_COUNT_FIELD_OFFSET;

	static {
		long stringValueFieldOffset = -1L;
		long stringOffsetFieldOffset = -1L;
		long stringCountFieldOffset = -1L;

		if (UNSAFE != null) {
			try {
				// this should be:
				stringValueFieldOffset = UNSAFE.objectFieldOffset(String.class.getDeclaredField("value"));
				// try also:
				stringOffsetFieldOffset = UNSAFE.objectFieldOffset(String.class.getDeclaredField("offset"));
				stringCountFieldOffset = UNSAFE.objectFieldOffset(String.class.getDeclaredField("count"));
			} catch (Throwable ignore) {
			}
		}

		STRING_VALUE_FIELD_OFFSET = stringValueFieldOffset;
		STRING_OFFSET_FIELD_OFFSET = stringOffsetFieldOffset;
		STRING_COUNT_FIELD_OFFSET = stringCountFieldOffset;
	}

	/**
	 * Returns String characters in most performing way.
	 * If possible, the inner <code>char[]</code> will be returned.
	 * If not, <code>toCharArray()</code> will be called.
	 * Returns <code>null</code> when argument is <code>null</code>.
	 */
	static char[] unsafeGetChars(String string) {
		char[] value = (char[]) UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET);

		if (STRING_OFFSET_FIELD_OFFSET != -1) {
			// old String version with offset and count
			int offset = UNSAFE.getInt(string, STRING_OFFSET_FIELD_OFFSET);
			int count = UNSAFE.getInt(string, STRING_COUNT_FIELD_OFFSET);

			if (offset == 0 && count == value.length) {
				// no need to copy
				return value;

			} else {
				char[] result = new char[count];
				System.arraycopy(value, offset, result, 0, count);
				return result;
			}

		} else {
			return value;
		}
	}

	static String unsafeCreateString(char[] chars) {
		String mutable = new String();

		UNSAFE.putObject(mutable, STRING_VALUE_FIELD_OFFSET, chars);

		if (STRING_COUNT_FIELD_OFFSET != -1) {
			UNSAFE.putInt(mutable, STRING_COUNT_FIELD_OFFSET, chars.length);
		}

		return mutable;
	}

}