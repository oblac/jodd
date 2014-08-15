// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.JoddJson;
import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.CtorDescriptor;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.Getter;
import jodd.introspector.PropertyDescriptor;
import jodd.introspector.Setter;
import jodd.json.meta.JsonAnnotationManager;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.CharUtil;
import jodd.util.ClassLoaderUtil;
import jodd.util.StringPool;
import jodd.util.UnsafeUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple, developer-friendly JSON parser. It focuses on easy usage
 * and type mappings. Uses Jodd's type converters, so it is natural
 * companion for Jodd projects.
 * <p>
 * See: http://www.ietf.org/rfc/rfc4627.txt
 */
public class JsonParser {

	private static final char[] T_RUE = new char[] {'r', 'u', 'e'};
	private static final char[] F_ALSE = new char[] {'a', 'l', 's', 'e'};
	private static final char[] N_ULL = new char[] {'u', 'l', 'l'};

	private static final String KEYS = "keys";
	private static final String VALUES = "values";

	private static final MapToBean mapToBean = new MapToBean();

	protected int ndx = 0;
	protected char[] input;
	protected int total;
	protected Path path;

	public JsonParser() {
		text = new char[512];
	}

	/**
	 * Resets JSON parser, so it can be reused.
	 */
	protected void reset() {
		this.ndx = 0;
		this.textLen = 0;
		this.path = new Path();
	}

	// ---------------------------------------------------------------- mappings

	protected Map<Path, Class> mappings;

	/**
	 * Maps a class to JSONs root.
	 */
	public JsonParser map(Class target) {
		return map(null, target);
	}

	/**
	 * Maps a class to given path. For arrays, append <code>values</code>
	 * to the path to specify component type (if not specified by
	 * generics).
	 */
	public JsonParser map(String path, Class target) {
		if (mappings == null) {
			mappings = new HashMap<Path, Class>();
		}
		mappings.put(Path.parse(path), target);
		return this;
	}

	/**
	 * Replaces type with mapped type for current path.
	 */
	protected Class replaceWithMappedTypeForPath(Class target) {
		if (mappings == null) {
			return target;
		}

		Class newType = mappings.get(path);

		if (newType == null) {
			return target;
		}

		return newType;
	}

	// ---------------------------------------------------------------- converters

	protected Map<Path, ValueConverter> convs;

	/**
	 * Defines {@link jodd.json.ValueConverter} to use on given path.
	 */
	public JsonParser use(String path, ValueConverter valueConverter) {
		if (convs == null) {
			convs = new HashMap<Path, ValueConverter>();
		}
		convs.put(Path.parse(path), valueConverter);
		return this;
	}

	/**
	 * Lookups for value converter for current path.
	 */
	protected ValueConverter lookupValueConverter() {
		if (convs == null) {
			return null;
		}
		return convs.get(path);
	}

	// ---------------------------------------------------------------- class meta data name

	protected String classMetadataName = JoddJson.classMetadataName;

	/**
	 * Sets local class meta-data name.
	 */
	public JsonParser setClassMetadataName(String name) {
		classMetadataName = name;
		return this;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parses input JSON as given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T parse(String input, Class<T> targetType) {
		char[] chars = UnsafeUtil.getChars(input);
		return _parse(chars, targetType);
	}

	/**
	 * Parses input JSON string.
	 */
	public <T> T parse(String input) {
		char[] chars = UnsafeUtil.getChars(input);
		return _parse(chars, null);
	}

	/**
	 * Parses input JSON as given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T parse(char[] input, Class<?> targetType) {
		return _parse(input, targetType);
	}

	/**
	 * Parses input JSON char array.
	 */
	public <T> T parse(char[] input) {
		return _parse(input, null);
	}

	private <T> T _parse(char[] input, Class targetType) {
		this.input = input;
		this.total = input.length;

		reset();

		skipWhiteSpaces();

		Object value;

		try {
			value = parseValue(targetType, null, null);
		}
		catch (IndexOutOfBoundsException iofbex) {
			syntaxError("End of JSON");
			return null;
		}

		skipWhiteSpaces();

		if (ndx != total) {
			syntaxError("Trailing chars");
			return null;
		}

		return (T) value;
	}

	// ---------------------------------------------------------------- parser

	/**
	 * Parses a JSON value.
	 * @param targetType target type to convert, may be <code>null</code>
	 * @param componentType component type for maps and arrays, may be <code>null</code>
	 */
	protected Object parseValue(Class targetType, Class keyType, Class componentType) {
		ValueConverter valueConverter;

		char c = input[ndx];

		switch (c) {
			case '"':
				ndx++;
				Object string = parseStringContent();

				valueConverter = lookupValueConverter();
				if (valueConverter != null) {
					return valueConverter.convert(string);
				}

				if (targetType != null && targetType != String.class) {
					string = convertType(string, targetType);
				}
				return string;

			case '{':
				ndx++;
				return parseObjectContent(targetType, keyType, componentType);

			case '[':
				ndx++;
				return parseArrayContent(targetType, componentType);

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '-':
				Object number = parseNumber();

				valueConverter = lookupValueConverter();
				if (valueConverter != null) {
					return valueConverter.convert(number);
				}

				if (targetType != null) {
					number = convertType(number, targetType);
				}
				return number;

			case 'n':
				ndx++;
				if (match(N_ULL)) {
					valueConverter = lookupValueConverter();
					if (valueConverter != null) {
						return valueConverter.convert(null);
					}
					return null;
				}
				break;

			case 't':
				ndx++;
				if (match(T_RUE)) {
					Object value = Boolean.TRUE;

					valueConverter = lookupValueConverter();
					if (valueConverter != null) {
						return valueConverter.convert(value);
					}

					if (targetType != null) {
						value = convertType(value, targetType);
					}
					return value;
				}
				break;

			case 'f':
				ndx++;
				if (match(F_ALSE)) {
					Object value = Boolean.FALSE;

					valueConverter = lookupValueConverter();
					if (valueConverter != null) {
						return valueConverter.convert(value);
					}

					if (targetType != null) {
						value = convertType(value, targetType);
					}
					return value;
				}
				break;
		}

		syntaxError("Invalid char: " + input[ndx]);
		return null;
	}

	// ---------------------------------------------------------------- string

	protected char[] text;
	protected int textLen;

	/**
	 * Parses a string.
	 */
	protected String parseString() {
		consume('"');
		return parseStringContent();
	}

	/**
	 * Parses string content, once when starting quote has been consumer.
	 */
	protected String parseStringContent() {
		int startNdx = ndx;

		// roullout until the end of the string or the escape char
		while (true) {
			char c = input[ndx];

			if (c == '\"') {
				// no escapes found, just use existing string
				ndx++;
				return new String(input, startNdx, ndx - startNdx - 1);
			}

			if (c == '\\') {
				break;
			}

			ndx++;
		}

		// escapes found, proceed differently

		textLen = ndx - startNdx;

		if (textLen >= text.length) {
			grow();
		}

		System.arraycopy(input, startNdx, text, 0, textLen);

		// escape char, process everything until the end
		while (true) {
			char c = input[ndx];

			if (c == '\"') {
				// done
				ndx++;
				String str = new String(text, 0, textLen);
				textLen = 0;
				return str;
			}

			if (c == '\\') {
				// escape char found
				ndx++;

				c = input[ndx];

				switch (c) {
					case '\"' : c = '\"'; break;
					case '\\' : c = '\\'; break;
					case '/' : c = '/'; break;
					case 'b' : c = '\b'; break;
					case 'f' : c = '\f'; break;
					case 'n' : c = '\n'; break;
					case 'r' : c = '\r'; break;
					case 't' : c = '\t'; break;
					case 'u' :
						ndx++;
						c = parseUnicode();
						break;
					default:
						syntaxError("Invalid escape char: " + c);
				}
			}

			text[textLen] = c;

			textLen++;

			if (textLen >= text.length) {
				grow();
			}

			ndx++;
		}
	}

	/**
	 * Grows text array.
	 */
	protected void grow() {
		int newSize = text.length << 1;

		char[] newText = new char[newSize];

		if (textLen > 0) {
			System.arraycopy(text, 0, newText, 0, textLen);
		}

		text = newText;
	}

	/**
	 * Parses 4 characters and returns unicode character.
	 */
	protected char parseUnicode() {
		int i0 = CharUtil.hex2int(input[ndx++]);
		int i1 = CharUtil.hex2int(input[ndx++]);
		int i2 = CharUtil.hex2int(input[ndx++]);
		int i3 = CharUtil.hex2int(input[ndx]);

		return (char) ((i0 << 12) + (i1 << 8) + (i2 << 4) + i3);
	}

	// ---------------------------------------------------------------- number

	/**
	 * Parses JSON numbers.
	 */
	protected Number parseNumber() {
		int startIndex = ndx;

		char c = input[ndx];

		boolean isDouble = false;
		boolean isExp = false;

		if (c == '-') {
			ndx++;
		}

		while (true) {
			if (isEOF()) {
				break;
			}

			c = input[ndx];

			if (c >= '0' && c <= '9') {
				ndx++;
				continue;
			}
			if (c <= 32) {		// white space
				break;
			}
			if (c == ',' || c == '}' || c == ']') {	// delimiter
				break;
			}

			if (c == '.') {
				isDouble = true;
			}
			else if (c == 'e' || c == 'E') {
				isExp = true;
			}
			ndx++;
		}

		String value = String.valueOf(input, startIndex, ndx - startIndex);

		if (isDouble) {
			return Double.valueOf(value);
		}

		long longNumber;

		if (isExp) {
			longNumber = Double.valueOf(value).longValue();
		}
		else {
			if (value.length() >= 19) {
				// if string is 19 chars and longer, it can be over the limit
				BigInteger bigInteger = new BigInteger(value);

				if (isGreaterThenLong(bigInteger)) {
					return bigInteger;
				}
				longNumber = bigInteger.longValue();
			}
			else {
				longNumber = Long.parseLong(value);
			}
		}

		if ((longNumber > 0 && longNumber <= Integer.MAX_VALUE) || (longNumber < 0 && longNumber >= Integer.MIN_VALUE)) {
			return Integer.valueOf((int) longNumber);
		}
		return Long.valueOf(longNumber);
	}

	private static boolean isGreaterThenLong(BigInteger bigInteger) {
		if (bigInteger.compareTo(MAX_LONG) == 1) {
			return true;
		}
		if (bigInteger.compareTo(MIN_LONG) == -1) {
			return true;
		}
		return false;
	}

	private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
	private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

	// ---------------------------------------------------------------- array

	/**
	 * Parses arrays, once when open bracket has been consumed.
	 */
	protected Object parseArrayContent(Class targetType, Class componentType) {
		targetType = replaceWithMappedTypeForPath(targetType);

		if (componentType == null && targetType != null && targetType.isArray()) {
			componentType = targetType.getComponentType();
		}

		path.push(VALUES);

		componentType = replaceWithMappedTypeForPath(componentType);

		List<Object> target = newArrayInstance(targetType);

		boolean koma = false;

		mainloop:
		while (true) {
			skipWhiteSpaces();

			char c = input[ndx];

			if (c == ']') {
				if (koma) {
					syntaxError("Trailing comma");
				}

				ndx++;
				path.pop();
				return target;
			}

			Object value = parseValue(componentType, null, null);

			target.add(value);

			skipWhiteSpaces();

			c = input[ndx];

			switch (c) {
				case ']': ndx++; break mainloop;
				case ',': ndx++; koma = true; break;
				default: syntaxError("Invalid char: expected ] or ,");
			}

		}

		path.pop();

		if (targetType != null) {
			return convertType(target, targetType);
		}

		return target;
	}

	// ---------------------------------------------------------------- object

	/**
	 * Parses object, once when open bracket has been consumed.
	 */
	protected Object parseObjectContent(Class targetType, Class valueKeyType, Class valueType) {
		targetType = replaceWithMappedTypeForPath(targetType);

		Object target;
		boolean isTargetTypeMap = true;
		boolean isTargetRealTypeMap = true;
		ClassDescriptor targetTypeClassDescriptor = null;

		if (targetType != null) {
			targetTypeClassDescriptor = ClassIntrospector.lookup(targetType);

			// find if the target is really a map
			// because when classMetadataName != null we are forcing
			// map usage locally in this method

			isTargetRealTypeMap = targetTypeClassDescriptor.isMap();
		}

		if (isTargetRealTypeMap) {
			// resolve keys only for real maps
			path.push(KEYS);
			valueKeyType = replaceWithMappedTypeForPath(valueKeyType);
			path.pop();
		}

		if (classMetadataName == null) {
			// create instance of target type, no 'class' information
			target = newObjectInstance(targetType);

			isTargetTypeMap = isTargetRealTypeMap;
		} else {
			// all beans will be created first as a map
			target = new HashMap();
		}

		boolean koma = false;

		mainloop:
		while (true) {
			skipWhiteSpaces();

			char c = input[ndx];

			if (c == '}') {
				if (koma) {
					syntaxError("Trailing comma");
				}

				ndx++;
				break;
			}

			koma = false;

			String key = parseString();

			skipWhiteSpaces();

			consume(':');

			skipWhiteSpaces();

			// read the type of the simple property

			PropertyDescriptor pd = null;
			Class propertyType = null;
			Class keyType = null;
			Class componentType = null;

			// resolve simple property

			if (!isTargetRealTypeMap) {
				// replace key with real property value
				key = JsonAnnotationManager.getInstance().resolveRealName(targetType, key);
			}

			if (!isTargetTypeMap) {
				pd = targetTypeClassDescriptor.getPropertyDescriptor(key, true);

				if (pd != null) {
					propertyType = pd.getType();
					keyType = resolveKeyType(pd);
					componentType = resolveComponentType(pd);
				}
			}

			Object value;

			if (!isTargetTypeMap) {
				// *** inject into bean
				path.push(key);

				value = parseValue(propertyType, keyType, componentType);

				path.pop();

				if (pd != null) {
					// only inject values if target property exist
					injectValueIntoObject(target, pd, value);
				}
			}
			else {
				Object keyValue = key;

				if (valueKeyType != null) {
					keyValue = convertType(key, valueKeyType);
				}

				// *** add to map
				if (isTargetRealTypeMap) {
					path.push(VALUES);
				} else {
					path.push(key);
				}

				value = parseValue(valueType, null, null);

				path.pop();

				((Map) target).put(keyValue, value);
			}

			skipWhiteSpaces();

			c = input[ndx];

			switch (c) {
				case '}': ndx++; break mainloop;
				case ',': ndx++; koma = true; break;
				default: syntaxError("Invalid char: expected } or ,");
			}
		}

		// done

		// convert Map to target type
		if (classMetadataName != null) {
			Map map = (Map) target;

			String className = (String) map.get(classMetadataName);

			if (className != null) {
				try {
					targetType = ClassLoaderUtil.loadClass(className);
				} catch (ClassNotFoundException cnfex) {
					throw new JsonException(cnfex);
				}
			}

			// do conversion
			Object newTarget = newObjectInstance(targetType);

			mapToBean.map2bean(map, newTarget, classMetadataName);

			target = newTarget;
		}

		return target;
	}

	// ---------------------------------------------------------------- scanning tools

	/**
	 * Consumes char at current position. If char is different, throws the exception.
	 */
	protected void consume(char c) {
		if (input[ndx] != c) {
			syntaxError("Invalid char: expected " + c);
		}

		ndx++;
	}

	/**
	 * Returns <code>true</code> if scanning is at the end.
	 */
	protected boolean isEOF() {
		return ndx >= total;
	}

	/**
	 * Skips whitespaces. For the simplification, whitespaces are
	 * considered any characters less or equal to 32 (space).
	 */
	protected final void skipWhiteSpaces() {
		while (true) {
			if (isEOF()) {
				return;
			}
			if (input[ndx] > 32) {
				return;
			}
			ndx++;
		}
    }

	/**
	 * Matches char buffer with content on given location.
	 */
	protected final boolean match(char[] target) {
		for (char c : target) {
			if (input[ndx] != c) {
				return false;
			}
			ndx++;
		}

		return true;
	}

	// ---------------------------------------------------------------- object tools

	/**
	 * Creates new object or a <code>HashMap</code> if type is not specified.
	 */
	protected Object newObjectInstance(Class targetType) {
		if (targetType == null) {
			return new HashMap();
		}

		if (targetType == Map.class) {
			return new HashMap();
		}

		ClassDescriptor cd = ClassIntrospector.lookup(targetType);

		try {
			CtorDescriptor ctorDescriptor = cd.getDefaultCtorDescriptor(true);
			return ctorDescriptor.getConstructor().newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Creates new type for JSON array objects.
	 * It should (?) always return a list, for performance reasons.
	 * Later, the list will be converted into the target type.
	 */
	protected List<Object> newArrayInstance(Class targetType) {
		if (targetType == null || targetType == List.class || targetType.isArray()) {
			return new ArrayList<Object>();
		}

		try {
			return (List) targetType.newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Resolves key type for given property descriptor.
	 */
	protected Class resolveKeyType(PropertyDescriptor pd) {
		Class keyType = null;

		Getter getter = pd.getGetter(true);

		if (getter != null) {
			keyType = getter.getGetterRawKeyComponentType();
		}

		if (keyType == null) {
			FieldDescriptor fieldDescriptor = pd.getFieldDescriptor();

			if (fieldDescriptor != null) {
				keyType = fieldDescriptor.getRawKeyComponentType();
			}
		}

		return keyType;
	}

	/**
	 * Resolves component type for given property descriptor.
	 */
	protected Class resolveComponentType(PropertyDescriptor pd) {
		Class componentType = null;

		Getter getter = pd.getGetter(true);

		if (getter != null) {
			componentType = getter.getGetterRawComponentType();
		}

		if (componentType == null) {
			FieldDescriptor fieldDescriptor = pd.getFieldDescriptor();

			if (fieldDescriptor != null) {
				componentType = fieldDescriptor.getRawComponentType();
			}
		}

		return componentType;
	}

	/**
	 * Injects value into the targets property.
	 */
	protected void injectValueIntoObject(Object target, PropertyDescriptor pd, Object value) {
		Object convertedValue = value;

		if (value != null) {
			Class targetClass = pd.getType();

			convertedValue = convertType(value, targetClass);
		}

		try {
			Setter setter = pd.getSetter(true);
			if (setter != null) {
				setter.invokeSetter(target, convertedValue);
			}
			else {
				FieldDescriptor fd = pd.getFieldDescriptor();
				if (fd != null) {
					fd.getField().set(target, convertedValue);
				}
			}
		} catch (Exception ex) {
			throw new JsonException(ex);
		}
	}

	/**
	 * Converts type of the given value.
	 */
	protected Object convertType(Object value, Class targetType) {
		Class valueClass = value.getClass();

		if (valueClass == targetType) {
			return value;
		}

		try {
			return TypeConverterManager.convertType(value, targetType);
		}
		catch (Exception ex) {
			throw new JsonException("Type conversion failed", ex);
		}
	}

	// ---------------------------------------------------------------- error

	/**
	 * Throws {@link jodd.json.JsonException} indicating a syntax error.
	 */
	protected void syntaxError(String message) {
		String left = "...";
		String right = "...";
		int offset = 10;

		int from = ndx - offset;
		if (from < 0) {
			from = 0;
			left = StringPool.EMPTY;
		}

		int to = ndx + offset;
		if (to > input.length) {
			to = input.length;
			right = StringPool.EMPTY;
		}

		String str = String.valueOf(input, from, to - from);

		throw new JsonException(
				"Syntax error: " + message + "\n" +
				"offset: " + ndx + " near: \"" + left + str + right + "\"");
	}

}