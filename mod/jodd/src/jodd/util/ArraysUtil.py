import string;

f = open('ArraysUtil.java', 'w')
f.write('''
// Copyright (c) 2003-2011, Jodd Team (jodd.sf.net). All Rights Reserved.

package jodd.util;

import java.lang.reflect.Array;
import static jodd.util.StringPool.NULL;

/**
 * More array utilities.
 * <b>DO NOT MODIFY: this source is generated.</b> 
 */
public class ArraysUtil {

''')

types = ['String', 'byte', 'char', 'short', 'int', 'long', 'float', 'double', 'boolean']
prim_types = ['byte', 'char', 'short', 'int', 'long', 'float', 'double', 'boolean']
big_types = ['Byte', 'Character', 'Short', 'Integer', 'Long', 'Float', 'Double', 'Boolean']
prim_types_safe = ['byte', 'char', 'short', 'int', 'long', 'boolean']


f.write('\n\n\t// ---------------------------------------------------------------- merge')
f.write('''

	/**
	 * Merge arrays.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] merge(T[]... arrays) {
		Class componentType =  arrays.getClass().getComponentType().getComponentType();
		int length = 0;
		for (T[] array : arrays) {
			length += array.length;
		}
		T[] result = (T[]) Array.newInstance(componentType, length);

		length = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}

''')
template = '''
	/**
	 * Merge arrays.
	 */
	public static $T[] merge($T[]... arrays) {
		int length = 0;
		for ($T[] array : arrays) {
			length += array.length;
		}
		$T[] result = new $T[length];
		length = 0;
		for ($T[] array : arrays) {
			System.arraycopy(array, 0, result, length, array.length);
			length += array.length;
		}
		return result;
	}
'''
for type in types:
	data = template.replace('$T', type)
	f.write(data)



f.write('\n\n\t// ---------------------------------------------------------------- join')
f.write('''

	/**
	 * Joins two arrays.
	 */
	public static <T> T[] join(T[] first, T[] second) {
		return join(first, second, null);
	}

	/**
	 * Joins two arrays.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] join(T[] first, T[] second, Class componentType) {
		if (componentType == null) {
			componentType = first.getClass().getComponentType();
		}
		T[] temp = (T[]) Array.newInstance(componentType, first.length + second.length);
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}

''')
template = '''
	/**
	 * Joins two arrays.
	 */
	public static $T[] join($T[] first, $T[] second) {
		$T[] temp = new $T[first.length + second.length];
		System.arraycopy(first, 0, temp, 0, first.length);
		System.arraycopy(second, 0, temp, first.length, second.length);
		return temp;
	}
'''
for type in types:
	data = template.replace('$T', type)
	f.write(data)



f.write('\n\n\t// ---------------------------------------------------------------- resize')
f.write('''

	/**
	 * Resizes an array.
	 */
	public static <T> T[] resize(T[] buffer, int newSize) {
		return resize(buffer, newSize, null);
	}
		
	/**
	 * Resizes an array.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] resize(T[] buffer, int newSize, Class<?> componentType) {
		if (componentType == null) {
			componentType =  buffer.getClass().getComponentType();
		}
		T[] temp = (T[]) Array.newInstance(componentType, newSize);
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}
'''
)
template = '''
	/**
	 * Resizes an array.
	 */
	public static $T[] resize($T buffer[], int newSize) {
		$T temp[] = new $T[newSize];
		System.arraycopy(buffer, 0, temp, 0, buffer.length >= newSize ? newSize : buffer.length);
		return temp;
	}
'''
for type in types:
	data = template.replace('$T', type)
	f.write(data)


f.write('\n\n\t// ---------------------------------------------------------------- append')
f.write('''

	/**
	 * Appends an element to array.
	 */
	public static <T> T[] append(T[] buffer, T newElement) {
		T[] t = resize(buffer, buffer.length + 1, newElement.getClass());
		t[buffer.length] = newElement;
		return t;
	}
'''
)
template = '''
	/**
	 * Appends an element to array.
	 */
	public static $T[] append($T buffer[], $T newElement) {
		$T[] t = resize(buffer, buffer.length + 1);
		t[buffer.length] = newElement;
		return t;
	}
'''
for type in types:
	data = template.replace('$T', type)
	f.write(data)




f.write('\n\n\t// ---------------------------------------------------------------- subarray')
f.write('''

	/**
	 * Returns subarray.
	 */
	public static <T> T[] subarray(T[] buffer, int offset, int length) {
		return subarray(buffer, offset, length, null);
	}

	/**
	 * Returns subarray.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] subarray(T[] buffer, int offset, int length, Class componentType) {
		if (componentType == null) {
			componentType = buffer.getClass().getComponentType();
		}
		T[] temp = (T[]) Array.newInstance(componentType, length);
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}
''')
template = '''
	/**
	 * Returns subarray.
	 */
	public static $T[] subarray($T[] buffer, int offset, int length) {
		$T temp[] = new $T[length];
		System.arraycopy(buffer, offset, temp, 0, length);
		return temp;
	}
'''
for type in types:
	data = template.replace('$T', type)
	f.write(data)



f.write('\n\n\t// ---------------------------------------------------------------- insert')
f.write('''

	/**
	 * Inserts one array into another.
	 */
	public static <T> T[] insert(T[] dest, T[] src, int offset) {
		return insert(dest, src, offset, null);
	}

	/**
	 * Inserts one array into another.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] insert(T[] dest, T[] src, int offset, Class componentType) {
		if (componentType == null) {
			componentType = dest.getClass().getComponentType();
		}
		T[] temp = (T[]) Array.newInstance(componentType, dest.length + src.length);
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}
''')
template = '''
	/**
	 * Inserts one array into another.
	 */
	public static $T[] insert($T[] dest, $T[] src, int offset) {
		$T[] temp = new $T[dest.length + src.length];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset, temp, src.length + offset, dest.length - offset);
		return temp;
	}
'''
for type in types:
	data = template.replace('$T', type)
	f.write(data)

f.write('\n\n\t// ---------------------------------------------------------------- insertAt')
f.write('''

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static <T> T[] insertAt(T[] dest, T[] src, int offset) {
		return insertAt(dest, src, offset, null);
	}

	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> T[] insertAt(T[] dest, T[] src, int offset, Class componentType) {
		if (componentType == null) {
			componentType = dest.getClass().getComponentType();
		}
		T[] temp = (T[]) Array.newInstance(componentType, dest.length + src.length - 1);
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}
''')
template = '''
	/**
	 * Inserts one array into another by replacing specified offset.
	 */
	public static $T[] insertAt($T[] dest, $T[] src, int offset) {
		$T[] temp = new $T[dest.length + src.length - 1];
		System.arraycopy(dest, 0, temp, 0, offset);
		System.arraycopy(src, 0, temp, offset, src.length);
		System.arraycopy(dest, offset + 1, temp, src.length + offset, dest.length - offset - 1);
		return temp;
	}
'''
for type in types:
	data = template.replace('$T', type)
	f.write(data)

f.write('\n\n\t// ---------------------------------------------------------------- convert')
f.write('''

''')

template = '''
	/**
	 * Converts to primitive array.
	 */
	public static $t[] values($T[] array) {
		$t[] dest = new $t[array.length];
		for (int i = 0; i < array.length; i++) {
			$T v = array[i];
			if (v != null) {
				dest[i] = v.$tValue();
			}
		}
		return dest;
	}
	/**
	 * Converts to object array.
	 */
	public static $T[] valuesOf($t[] array) {
		$T[] dest = new $T[array.length];
		for (int i = 0; i < array.length; i++) {
			dest[i] = $T.valueOf(array[i]);
		}
		return dest;
	}

'''
for i in range(len(prim_types)):
	data = template.replace('$t', prim_types[i])
	data = data.replace('$T', big_types[i])
	f.write(data)

f.write('\n\n\t// ---------------------------------------------------------------- indexof')
f.write('''

''')
template = '''
	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf($T[] array, $T value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	public static boolean contains($T[] array, $T value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf($T[] array, $T value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf($T[] array, $T value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
'''
for type in prim_types_safe:
	data = template.replace('$T', type)
	f.write(data)

template = '''
	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf($T[] array, $T value) {
		for (int i = 0; i < array.length; i++) {
			if ($B.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}
	public static boolean contains($T[] array, $T value) {
		return indexOf(array, value) != -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf($T[] array, $T value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if ($B.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf($T[] array, $T value, int startIndex, int endIndex) {
		for (int i = startIndex; i < endIndex; i++) {
			if ($B.compare(array[i], value) == 0) {
				return i;
			}
		}
		return -1;
	}
'''

data = template.replace('$T', 'float')
data = data.replace('$B', 'Float')
f.write(data)
data = template.replace('$T', 'double')
data = data.replace('$B', 'Double')
f.write(data)

f.write('''
	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf(Object[] array, Object value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}
	public static boolean contains(Object[] array, Object value) {
		return indexOf(array, value) != -1;
	}

	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf(Object[] array, Object value, int startIndex) {
		for (int i = startIndex; i < array.length; i++) {
			if (array[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}
	public static boolean contains(Object[] array, Object value, int startIndex) {
		return indexOf(array, value, startIndex) != -1;
	}


''')


f.write('\n\n\t// ---------------------------------------------------------------- indexof 2')
f.write('''

''')
template = '''
	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf($T[] array, $T[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains($T[] array, $T[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf($T[] array, $T[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf($T[] array, $T[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		$T c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if (array[i] != c) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if (sub[j] != array[k]) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}
'''
for type in prim_types_safe:
	data = template.replace('$T', type)
	f.write(data)

template = '''
	/**
	 * Finds the first occurrence in an array.
	 */
	public static int indexOf($T[] array, $T[] sub) {
		return indexOf(array, sub, 0, array.length);
	}
	public static boolean contains($T[] array, $T[] sub) {
		return indexOf(array, sub) != -1;
	}


	/**
	 * Finds the first occurrence in an array from specified given position.
	 */
	public static int indexOf($T[] array, $T[] sub, int startIndex) {
		return indexOf(array, sub, startIndex, array.length);
	}

	/**
	 * Finds the first occurrence in an array from specified given position and upto given length.
	 */
	public static int indexOf($T[] array, $T[] sub, int startIndex, int endIndex) {
		int sublen = sub.length;
		if (sublen == 0) {
			return startIndex;
		}
		int total = endIndex - sublen + 1;
		$T c = sub[0];
	mainloop:
		for (int i = startIndex; i < total; i++) {
			if ($B.compare(array[i], c) != 0) {
				continue;
			}
			int j = 1;
			int k = i + 1;
			while (j < sublen) {
				if ($B.compare(sub[j], array[k]) != 0) {
					continue mainloop;
				}
				j++; k++;
			}
			return i;
		}
		return -1;
	}
'''

data = template.replace('$T', 'float')
data = data.replace('$B', 'Float')
f.write(data)
data = template.replace('$T', 'double')
data = data.replace('$B', 'Double')
f.write(data)


f.write('\n\n\t// ---------------------------------------------------------------- toString')
f.write('''

	/**
	 * Converts an array to string. Return string contains no brackets.
	 */
	public static String toString(Object[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',').append(' ');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}
'''
)
template = '''
	/**
	 * Converts an array to string. Return string contains no brackets.
	 */
	public static String toString($T[] array) {
		if (array == null) {
			return NULL;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				sb.append(',').append(' ');
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}
'''
for type in types:
	data = template.replace('$T', type)
	f.write(data)



f.write('}')
f.close()
