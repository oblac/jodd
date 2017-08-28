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

package jodd.json;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.PropertyDescriptor;
import jodd.json.meta.JsonAnnotationManager;
import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.util.UnsafeUtil;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jodd.json.JoddJson.DEFAULT_CLASS_METADATA_NAME;

/**
 * Simple, developer-friendly JSON parser. It focuses on easy usage
 * and type mappings. Uses Jodd's type converters, so it is natural
 * companion for Jodd projects.
 * <p>
 * See: http://www.ietf.org/rfc/rfc4627.txt
 */
public class JsonParser extends JsonParserBase {

	/**
	 * Static ctor.
	 */
	public static JsonParser create() {
		return new JsonParser();
	}

	private static final char[] T_RUE = new char[] {'r', 'u', 'e'};
	private static final char[] F_ALSE = new char[] {'a', 'l', 's', 'e'};
	private static final char[] N_ULL = new char[] {'u', 'l', 'l'};

	/**
	 * Map keys.
	 */
	public static final String KEYS = "keys";
	/**
	 * Array or map values.
	 */
	public static final String VALUES = "values";

	protected int ndx = 0;
	protected char[] input;
	protected int total;
	protected Path path;
	protected boolean useAltPaths = JoddJson.useAltPathsByParser;
	protected Class rootType;
	protected MapToBean mapToBean;
	protected boolean looseMode;

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
		if (useAltPaths) {
			path.altPath = new Path();
		}

		if (classMetadataName != null) {
			mapToBean = createMapToBean(classMetadataName);
		}
	}

	/**
	 * Enables usage of additional paths.
	 */
	public JsonParser useAltPaths() {
		this.useAltPaths = true;
		return this;
	}

	/**
	 * Enables 'loose' mode for parsing. When 'loose' mode is enabled,
	 * JSON parsers swallows also invalid JSONs:
	 * <ul>
	 *     <li>invalid escape character sequence is simply added to the output</li>
	 *     <li>strings can be quoted with single-quotes</li>
	 *     <li>strings can be unquoted, but may not contain escapes</li>
	 * </ul>
	 */
	public JsonParser looseMode(boolean looseMode) {
		this.looseMode = looseMode;
		return this;
	}

	// ---------------------------------------------------------------- mappings

	protected Map<Path, Class> mappings;

	/**
	 * Maps a class to JSONs root.
	 */
	public JsonParser map(Class target) {
		rootType = target;
		return this;
	}

	/**
	 * Maps a class to given path. For arrays, append <code>values</code>
	 * to the path to specify component type (if not specified by
	 * generics).
	 */
	public JsonParser map(String path, Class target) {
		if (path == null) {
			rootType = target;
			return this;
		}
		if (mappings == null) {
			mappings = new HashMap<>();
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

		Class newType;

		// first try alt paths

		Path altPath = path.getAltPath();

		if (altPath != null) {
			if (!altPath.equals(path)) {
				newType = mappings.get(altPath);

				if (newType != null) {
					return newType;
				}
			}
		}

		// now check regular paths

		newType = mappings.get(path);

		if (newType != null) {
			return newType;
		}

		return target;
	}

	// ---------------------------------------------------------------- converters

	protected Map<Path, ValueConverter> convs;

	/**
	 * Defines {@link jodd.json.ValueConverter} to use on given path.
	 */
	public JsonParser withValueConverter(String path, ValueConverter valueConverter) {
		if (convs == null) {
			convs = new HashMap<>();
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

	public JsonParser withClassMetadata(boolean useMetadata) {
		if (useMetadata) {
			classMetadataName = DEFAULT_CLASS_METADATA_NAME;
		}
		else {
			classMetadataName = null;
		}
		return this;
	}


	// ---------------------------------------------------------------- parse

	/**
	 * Parses input JSON as given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T parse(String input, Class<T> targetType) {
		char[] chars = UnsafeUtil.getChars(input);
		rootType = targetType;
		return _parse(chars);
	}

	/**
	 * Parses input JSON to {@link JsonObject}, special case of {@link #parse(String, Class)}.
	 */
	public JsonObject parseAsJsonObject(String input) {
		return new JsonObject(parse(input));
	}

	/**
	 * Parses input JSON to {@link JsonArray}, special case of parsing.
	 */
	public JsonArray parseAsJsonArray(String input) {
		return new JsonArray(parse(input));
	}

	/**
	 * Parses input JSON to a list with specified component type.
	 */
	public <T> List<T> parseAsList(String string, Class<T> componentType) {
		return new JsonParser()
			.map(JsonParser.VALUES, componentType)
			.parse(string);
	}

	/**
	 * Parses input JSON to a list with specified key and value types.
	 */
	public <K, V> Map<K, V> parseAsMap(
		String string, Class<K> keyType, Class<V> valueType) {

		return new JsonParser()
			.map(JsonParser.KEYS, keyType)
			.map(JsonParser.VALUES, valueType)
			.parse(string);
	}

	/**
	 * Parses input JSON string.
	 */
	public <T> T parse(String input) {
		char[] chars = UnsafeUtil.getChars(input);
		return _parse(chars);
	}

	/**
	 * Parses input JSON as given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T parse(char[] input, Class<T> targetType) {
		rootType = targetType;
		return _parse(input);
	}

	/**
	 * Parses input JSON char array.
	 */
	public <T> T parse(char[] input) {
		return _parse(input);
	}


	private <T> T _parse(char[] input) {
		this.input = input;
		this.total = input.length;

		reset();

		skipWhiteSpaces();

		Object value;

		try {
			value = parseValue(rootType, null, null);
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

		// convert map to target type

		if (classMetadataName != null && rootType == null) {
			if (value instanceof Map) {
				Map map = (Map) value;

				value = mapToBean.map2bean(map, null);
			}
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
			case '\'':
				if (!looseMode) {
					break;
				}
			case '"':
				ndx++;
				Object string = parseStringContent(c);

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

		if (looseMode) {
			// try to parse unquoted string
			Object string = parseUnquotedStringContent();

			valueConverter = lookupValueConverter();
			if (valueConverter != null) {
				return valueConverter.convert(string);
			}

			if (targetType != null && targetType != String.class) {
				string = convertType(string, targetType);
			}
			return string;
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
		char quote = '\"';
		if (looseMode) {
			quote = consumeOneOf('\"', '\'');
			if (quote == 0) {
				return parseUnquotedStringContent();
			}
		} else {
			consume(quote);
		}

		return parseStringContent(quote);
	}

	/**
	 * Parses string content, once when starting quote has been consumer.
	 */
	protected String parseStringContent(final char quote) {
		int startNdx = ndx;

		// roll-out until the end of the string or the escape char
		while (true) {
			char c = input[ndx];

			if (c == quote) {
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

		growEmpty();

		System.arraycopy(input, startNdx, text, 0, textLen);

		// escape char, process everything until the end
		while (true) {
			char c = input[ndx];

			if (c == quote) {
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
						if (looseMode) {
							if (c != '\'') {
								c = '\\';
								ndx--;
							}
						}
						else {
							syntaxError("Invalid escape char: " + c);
						}
				}
			}

			text[textLen] = c;

			textLen++;

			growAndCopy();

			ndx++;
		}
	}

	/**
	 * Grows empty text array.
	 */
	protected void growEmpty() {
		if (textLen >= text.length) {
			int newSize = textLen << 1;

			text = new char[newSize];
		}
	}

	/**
	 * Grows text array when {@code text.length == textLen}.
	 */
	protected void growAndCopy() {
		if (textLen == text.length) {
			int newSize = text.length << 1;

			char[] newText = new char[newSize];

			if (textLen > 0) {
				System.arraycopy(text, 0, newText, 0, textLen);
			}

			text = newText;
		}
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

	// ---------------------------------------------------------------- un-quoted

	private final static char[] UNQOUTED_DELIMETERS = ",:[]{}\\\"'".toCharArray();

	/**
	 * Parses un-quoted string content.
	 */
	protected String parseUnquotedStringContent() {
		int startNdx = ndx;

		while (true) {
			char c = input[ndx];

			if (c <= ' ' || CharUtil.equalsOne(c, UNQOUTED_DELIMETERS)) {
				// done
				int len = ndx - startNdx;

				skipWhiteSpaces();

				return new String(input, startNdx, len);
			}

			ndx++;
		}
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

		if ((longNumber >= Integer.MIN_VALUE) && (longNumber <= Integer.MAX_VALUE)) {
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
		// detect special case

		if (targetType == Object.class) {
			targetType = List.class;
		}

		// continue

		targetType = replaceWithMappedTypeForPath(targetType);

		if (componentType == null && targetType != null && targetType.isArray()) {
			componentType = targetType.getComponentType();
		}

		path.push(VALUES);

		componentType = replaceWithMappedTypeForPath(componentType);

		Collection<Object> target = newArrayInstance(targetType);

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
		// detect special case

		if (targetType == Object.class) {
			targetType = Map.class;
		}

		// continue

		targetType = replaceWithMappedTypeForPath(targetType);

		Object target;
		boolean isTargetTypeMap = true;
		boolean isTargetRealTypeMap = true;
		ClassDescriptor targetTypeClassDescriptor = null;
		JsonAnnotationManager.TypeData typeData = null;

		if (targetType != null) {
			targetTypeClassDescriptor = ClassIntrospector.lookup(targetType);

			// find if the target is really a map
			// because when classMetadataName != null we are forcing
			// map usage locally in this method

			isTargetRealTypeMap = targetTypeClassDescriptor.isMap();

			typeData = JoddJson.annotationManager.lookupTypeData(targetType);
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
			String keyOriginal = key;

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
				key = JoddJson.annotationManager.resolveRealName(targetType, key);
			}

			if (!isTargetTypeMap) {
				pd = targetTypeClassDescriptor.getPropertyDescriptor(key, true);

				if (pd != null) {
					propertyType = pd.getType();
					keyType = pd.resolveKeyType(true);
					componentType = pd.resolveComponentType(true);
				}
			}

			Object value;

			if (!isTargetTypeMap) {
				// *** inject into bean
					path.push(key);

					value = parseValue(propertyType, keyType, componentType);

					path.pop();

				if (typeData.rules.match(keyOriginal, !typeData.strict)) {

					if (pd != null) {
						// only inject values if target property exist
						injectValueIntoObject(target, pd, value);
					}
				}
			}
			else {
				Object keyValue = key;

				if (valueKeyType != null) {
					keyValue = convertType(key, valueKeyType);
				}

				// *** add to map
				if (isTargetRealTypeMap) {
					path.push(VALUES, key);

					valueType = replaceWithMappedTypeForPath(valueType);
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
			target = mapToBean.map2bean((Map) target, targetType);
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
	 * Consumes one of the allowed char at current position.
	 * If char is different, return <code>0</code>.
	 * If matched, returns matched char.
	 */
	protected char consumeOneOf(char c1, char c2) {
		char c = input[ndx];

		if ((c != c1) && (c != c2)) {
			return 0;
		}

		ndx++;

		return c;
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
				"Syntax error! " + message + "\n" +
				"offset: " + ndx + " near: \"" + left + str + right + "\"");
	}

}