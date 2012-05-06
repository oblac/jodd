
f = open('ConvertBean.java', 'r')
java = f.read()
f.close()

genStart = java.find('@@generated')
java = java[0:genStart + 11]

### -----------------------------------------------------------------

types = [
	[0, 'Boolean', 'boolean', 'false'],
	[2, 'Integer', 'int', '0'],
	[4, 'Long', 'long', '0'],
	[6, 'Float', 'float', '0'],
	[8, 'Double', 'double', '0'],
	[10, 'Short', 'short', '(short) 0'],
	[12, 'Character', 'char', '(char) 0'],
	[14, 'Byte', 'byte', '(byte) 0'],
]

template = '''

	/**
	 * Converts value to <code>$T</code>.
	 */
	public $T to$T(Object value) {
		return ($T) typeConverters[#].convert(value);
	}

	/**
	 * Converts value to <code>$T</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public $T to$T(Object value, $T defaultValue) {
		$T result = ($T) typeConverters[#].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}

	/**
	 * Converts value to <code>$t</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public $t to$PValue(Object value, $t defaultValue) {
		$T result = ($T) typeConverters[#++].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.$tValue();
	}

	/**
	 * Converts value to <code>$t</code> with common default value.
	 */
	public $t to$PValue(Object value) {
		return to$PValue(value, $D);
	}
'''

for type in types:
	# small type
	data = template
	data = data.replace('#++', str(type[0] + 1))
	data = data.replace('#', str(type[0]))
	data = data.replace('$T', type[1])
	data = data.replace('$t', type[2])
	data = data.replace('$P', type[2].title())
	data = data.replace('$D', type[3])
	java += data

### -----------------------------------------------------------------

types = [
	[16, 'boolean[]', 'BooleanArray',	0],
	[17, 'int[]', 'IntegerArray', 		0],
	[18, 'long[]', 'LongArray', 		0],
	[19, 'float[]', 'FloatArray', 		0],
	[20, 'double[]', 'DoubleArray', 	0],
	[21, 'short[]', 'ShortArray', 		0],
	[22, 'char[]', 'CharacterArray', 	0],
	[23, 'String', 'String', 			1],
	[24, 'String[]', 'StringArray', 	0],
	[25, 'Class', 'Class', 				0],
	[26, 'Class[]', 'ClassArray', 		0],
	[27, 'JDateTime', 'JDateTime', 		1],
	[28, 'Date', 'Date', 				1],
	[29, 'Calendar', 'Calendar', 		1],
	[30, 'BigInteger', 'BigInteger', 	1],
	[31, 'BigDecimal', 'BigDecimal', 	1],
]

template = '''
	/**
	 * Converts value to <code>$T</code>.
	 */
	public $T to$N(Object value) {
		return ($T) typeConverters[#].convert(value);
	}
'''
template2 = '''
	/**
	 * Converts value to <code>$T</code>. Returns default value
	 * when conversion result is <code>null</code>
	 */
	public $T to$N(Object value, $T defaultValue) {
		$T result = ($T) typeConverters[#].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result;
	}
'''

for type in types:
	# small type
	data = template
	data = data.replace('#', str(type[0]))
	data = data.replace('$T', type[1])
	data = data.replace('$N', type[2])
	java += data

	if type[3] == 1:
		data = template2
		data = data.replace('#', str(type[0]))
		data = data.replace('$T', type[1])
		data = data.replace('$N', type[2])
		java += data

### -----------------------------------------------------------------

java += '}'

f = open('ConvertBean.java', 'w')
f.write(java)
f.close()