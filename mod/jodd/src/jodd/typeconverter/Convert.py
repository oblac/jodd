import string;

f = open('Convert.java', 'w')
f.write('''
// Copyright (c) 2003-2010, Jodd Team (jodd.sf.net). All Rights Reserved.

package jodd.typeconverter;

import jodd.typeconverter.impl.*;
import jodd.mutable.*;
import jodd.datetime.JDateTime;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;

/**
 * One class for simplifier and direct conversions to destination types.
 * <b>DO NOT MODIFY: this source is generated.</b>
 */
public class Convert {

''')

types = [
	['BigDecimal', 'null'],
	['BigInteger', 'null'],
	['boolean', 'Boolean', 'false'],
	['boolean[]', 'BooleanArray'],
	['byte', 'Byte', '(byte)0'],
	['byte[]', 'ByteArray'],
	['char', 'Character', '(char)0'],
	['Class', 'null'],
	['Class[]', 'ClassArray'],
	['double', 'Double', '(double)0'],
	['double[]', 'DoubleArray'],
	['float', 'Float', '(float)0'],
	['float[]', 'FloatArray'],
	['int', 'Integer', '0'],
	['int[]', 'IntegerArray'],
	['JDateTime', 'null'],
	['long', 'Long', '0'],
	['long[]', 'LongArray'],
	['MutableByte', 'null'],
	['MutableDouble', 'null'],
	['MutableFloat', 'null'],
	['MutableInteger', 'null'],
	['MutableLong', 'null'],
	['MutableShort', 'null'],
	['short', 'Short', '(short)0'],
	['short[]', 'ShortArray'],
	['String', 'null'],
	['String[]', 'StringArray'],
	['URI', 'null'],
	['URL', 'null'],

]

template = '''

	/**
	 * Converts value to <code>$T</code>.
	 */
	public static $T to$T(Object value) {
		return to$T(value, $1);
	}

	/**
	 * Converts value to <code>$T</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static $T to$T(Object value, $T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return $TConverter.valueOf(value);
	}
'''

template2 = '''

	/**
	 * Converts value to <code>$t</code>.
	 */
	public static $t to$T(Object value) {
		return to$T(value, $1);
	}

	/**
	 * Converts value to <code>$T</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static $t to$T(Object value, $t defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return $TConverter.valueOf(value).$tValue();
	}

	/**
	 * Converts value to <code>$T</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static $T to$T(Object value, $T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return $TConverter.valueOf(value);
	}
'''

templateA = '''

	/**
	 * Converts value to <code>$T</code>.
	 */
	public static $T to$1(Object value) {
		return to$1(value, null);
	}

	/**
	 * Converts value to <code>$T</code>. Returns default value
	 * when value is <code>null</code>.
	 */
	public static $T to$1(Object value, $T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return $1Converter.valueOf(value);
	}
'''








for type in types:
	if len(type) == 2:
		if (type[0][-2:] == '[]'):
			# array
			data = templateA
			data = data.replace('$T', type[0])
			data = data.replace('$1', type[1])
		else:
			# big type
			data = template
			data = data.replace('$T', type[0])
			data = data.replace('$1', type[1])
	else:
		# small type
		data = template2
		data = data.replace('$t', type[0])
		data = data.replace('$T', type[1])
		data = data.replace('$1', type[2])

	f.write(data)


f.write('''

	public static Object toObject(Object value, Class destinationType) {
		if (value == null) {
			return null;
		}
		TypeConverter converter = TypeConverterManager.lookup(destinationType);
		if (converter == null) {
			throw new TypeConversionException("Unable to convert value to type: '" + destinationType.getName() + "'.");
		}
		return converter.convert(value);
	}


''')

f.write('}')
f.close()
