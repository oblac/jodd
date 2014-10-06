// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * Few methods using infamous <code>java.misc.Unsafe</code>, mostly for private use.
 * See: http://mishadoff.github.io/blog/java-magic-part-4-sun-dot-misc-dot-unsafe/
 *
 * Thanx to Gatling (http://gatling-tool.org)!
 */
public class UnsafeUtil {

	public static final Unsafe UNSAFE;
	private static final long STRING_VALUE_FIELD_OFFSET;
	private static final long STRING_OFFSET_FIELD_OFFSET;
	private static final long STRING_COUNT_FIELD_OFFSET;

	static {
		Unsafe unsafe;
		try {
			Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			unsafe = (Unsafe) unsafeField.get(null);
		} catch (Throwable ignore) {
			unsafe = null;
		}

		long stringValueFieldOffset = -1L;
		long stringOffsetFieldOffset = -1L;
		long stringCountFieldOffset = -1L;

		if (unsafe != null) {
			try {
				// this should be:
				stringValueFieldOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
				// try also:
				stringOffsetFieldOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("offset"));
				stringCountFieldOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("count"));
			} catch (Throwable ignore) {
			}
		}

		UNSAFE = unsafe;
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
	public static char[] getChars(String string) {
		if (string == null) {
			return null;
		}
		if (UNSAFE == null) {
			return string.toCharArray();
		}

		char[] value = (char[]) UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET);

		if (STRING_OFFSET_FIELD_OFFSET != -1) {
			// old String version with offset and count
			int offset = UNSAFE.getInt(string, STRING_OFFSET_FIELD_OFFSET);
			int count = UNSAFE.getInt(string, STRING_COUNT_FIELD_OFFSET);

			if (offset == 0 && count == value.length) {
				// no need to copy
				return value;

			} else {
				char result[] = new char[count];
				System.arraycopy(value, offset, result, 0, count);
				return result;
			}

		} else {
			return value;
		}
	}

	/**
	 * Creates (mutable) string from given char array.
	 */
	public static String createString(char[] chars) {
		if (chars == null) {
			return null;
		}
		if (UNSAFE == null) {
			return new String(chars);
		}

		String mutable = new String();
		UNSAFE.putObject(mutable, STRING_VALUE_FIELD_OFFSET, chars);
		if (STRING_COUNT_FIELD_OFFSET != -1) {
			UNSAFE.putInt(mutable, STRING_COUNT_FIELD_OFFSET, chars.length);
		}
		return mutable;
	}

}