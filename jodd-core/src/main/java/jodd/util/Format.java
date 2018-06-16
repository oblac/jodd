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

public class Format {

	public static String alignLeftAndPad(final String text, final int size) {
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

	public static String alignRightAndPad(final String text, final int size) {
		int textLength = text.length();
		if (textLength > size) {
			return text.substring(textLength - size, textLength);
		}

		final StringBuilder sb = new StringBuilder(size);
		while (textLength++ < size) {
			sb.append(' ');
		}
		sb.append(text);
		return sb.toString();
	}

	/**
	 * Formats byte size to human readable bytecount.
	 * https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java/3758880#3758880
	 */
	public static String humanReadableByteCount(final long bytes, final boolean useSi) {
		final int unit = useSi ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		final int exp = (int) (Math.log(bytes) / Math.log(unit));
		final String pre = (useSi ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (useSi ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	/**
	 * Converts object into pretty string. All arrays are iterated.
	 */
	public static String toPrettyString(final Object value) {
		if (value == null) {
			return StringPool.NULL;
		}

		Class<?> type = value.getClass();

		if (type.isArray()) {
			Class componentType = type.getComponentType();

			if (componentType.isPrimitive()) {
				StringBuilder sb = new StringBuilder();
				sb.append('[');

				if (componentType == int.class) {
					sb.append(ArraysUtil.toString((int[]) value));
				}
				else if (componentType == long.class) {
					sb.append(ArraysUtil.toString((long[]) value));
				}
				else if (componentType == double.class) {
					sb.append(ArraysUtil.toString((double[]) value));
				}
				else if (componentType == float.class) {
					sb.append(ArraysUtil.toString((float[]) value));
				}
				else if (componentType == boolean.class) {
					sb.append(ArraysUtil.toString((boolean[]) value));
				}
				else if (componentType == short.class) {
					sb.append(ArraysUtil.toString((short[]) value));
				}
				else if (componentType == byte.class) {
					sb.append(ArraysUtil.toString((byte[]) value));
				} else {
					throw new IllegalArgumentException();
				}
				sb.append(']');
				return sb.toString();
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append('[');

				Object[] array = (Object[]) value;
				for (int i = 0; i < array.length; i++) {
					if (i > 0) {
						sb.append(',');
					}
					sb.append(toPrettyString(array[i]));
				}
				sb.append(']');
				return sb.toString();
			}
		} else if (value instanceof Iterable) {
			Iterable iterable = (Iterable) value;
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			int i = 0;
			for (Object o : iterable) {
				if (i > 0) {
					sb.append(',');
				}
				sb.append(toPrettyString(o));
				i++;
			}
			sb.append('}');
			return sb.toString();
		}

		return value.toString();
	}



}
