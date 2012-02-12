
f = open('ConvertBean.java', 'r')
java = f.read()
f.close()

genStart = java.find('@@generated')
java = java[0:genStart + 11]

### -----------------------------------------------------------------

types = [
	[0, 'Boolean', 'boolean', 'false'],
	[1, 'Integer', 'int', '0'],
	[2, 'Long', 'long', '0'],
	[3, 'Float', 'float', '0'],
	[4, 'Double', 'double', '0'],
	[5, 'Short', 'short', '(short) 0'],
	[6, 'Character', 'char', '(char) 0'],
]

template = '''

	/**
	 * Converts value to <code>$T</code>.
	 */
	public $T to$T(Object value) {
		return ($T) typeConverters[#].convert(value);
	}

	/**
	 * Converts value to <code>$t</code>. Returns default value
	 * when conversion result is <code>null</code>.
	 */
	public $t to$T(Object value, $t defaultValue) {
		$T result = ($T) typeConverters[#].convert(value);
		if (result == null) {
			return defaultValue;
		}
		return result.$tValue();
	}

	/**
	 * Converts value to <code>$t</code> with common default value.
	 */
	public $t to$TValue(Object value) {
		return to$T(value, $D);
	}
'''

for type in types:
	# small type
	data = template
	data = data.replace('#', str(type[0]))
	data = data.replace('$T', type[1])
	data = data.replace('$t', type[2])
	data = data.replace('$D', type[3])
	java += data

### -----------------------------------------------------------------

types = [
	[7, 'boolean[]', 'BooleanArray'],
	[8, 'int[]', 'IntegerArray'],
	[9, 'long[]', 'LongArray'],
	[10, 'float[]', 'FloatArray'],
	[11, 'double[]', 'DoubleArray'],
	[12, 'short[]', 'ShortArray'],
	[13, 'char[]', 'CharacterArray'],
	[14, 'String', 'String'],
	[15, 'String[]', 'StringArray'],
	[16, 'Class', 'Class'],
	[17, 'Class[]', 'ClassArray'],
	[18, 'JDateTime', 'JDateTime'],
	[19, 'Date', 'Date'],
	[20, 'Calendar', 'Calendar'],
	[21, 'BigInteger', 'BigInteger'],
	[22, 'BigDecimal', 'BigDecimal'],
]

template = '''

	/**
	 * Converts value to <code>$T</code>.
	 */
	public $T to$N(Object value) {
		return ($T) typeConverters[#].convert(value);
	}
'''

for type in types:
	# small type
	data = template
	data = data.replace('#', str(type[0]))
	data = data.replace('$T', type[1])
	data = data.replace('$N', type[2])
	java += data


### -----------------------------------------------------------------

java += '}'

f = open('ConvertBean.java', 'w')
f.write(java)
f.close()