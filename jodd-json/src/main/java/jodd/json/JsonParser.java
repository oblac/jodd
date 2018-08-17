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
import jodd.json.meta.TypeData;
import jodd.util.CharArraySequence;
import jodd.util.CharUtil;
import jodd.util.StringPool;
import jodd.util.UnsafeUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Simple, developer-friendly JSON parser. It focuses on easy usage
 * and type mappings. Uses Jodd's type converters, so it is natural
 * companion for Jodd projects.
 * <p>
 * This JSON parser also works in {@link #lazy(boolean)} mode. This
 * mode is for top performance usage: parsing is done very, very lazy.
 * While you can use all the mappings and other tools, for best performance
 * the lazy mode should be used only with maps and lists (no special mappings).
 * Also, the performance has it's price: more memory consumption, because the
 * original input is hold until the result is in use.
 * <p>
 * See: http://www.ietf.org/rfc/rfc4627.txt
 */
public class JsonParser extends JsonParserBase {

	public static class Defaults {

		public static final String DEFAULT_CLASS_METADATA_NAME = "__class";

		/**
		 * Flag for enabling the lazy mode.
		 */
		public static boolean lazy = false;
		/**
		 * Defines if parser will use extended paths information
		 * and path matching.
		 */
		public static boolean useAltPathsByParser = false;
		/**
		 * Default value for loose mode.
		 */
		public static boolean loose = false;

		/**
		 * Specifies if 'class' metadata is used and its value. When set, class metadata
		 * is used by {@link jodd.json.JsonSerializer} and all objects
		 * will have additional field with the class type in the resulting JSON.
		 * {@link jodd.json.JsonParser} will also consider this flag to build
		 * correct object type. If <code>null</code>, class information is not used.
		 */
		public static String classMetadataName = null;
	}

	/**
	 * Static ctor.
	 */
	public static JsonParser create() {
		return new JsonParser();
	}

	/**
	 * Creates a lazy implementation of the JSON parser.
	 */
	public static JsonParser createLazyOne() {
		return new JsonParser().lazy(true);
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
	protected boolean useAltPaths = Defaults.useAltPathsByParser;
	protected boolean lazy = Defaults.lazy;
	protected boolean looseMode = Defaults.loose;
	protected Class rootType;
	protected MapToBean mapToBean;
	private boolean notFirstObject = false;

	private final JsonAnnotationManager jsonAnnotationManager;

	public JsonParser() {
		this.text = new char[512];
		this.jsonAnnotationManager = JsonAnnotationManager.get();
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
	public JsonParser looseMode(final boolean looseMode) {
		this.looseMode = looseMode;
		return this;
	}

	/**
	 * Defines how JSON parser works. In non-lazy mode, the whole JSON is parsed as it is.
	 * In the lazy mode, not everything is parsed, but some things are left lazy.
	 * This way we gain performance, especially on partial usage of the whole JSON.
	 * However, be aware that parser holds the input memory until the returned
	 * objects are disposed.
	 */
	public JsonParser lazy(final boolean lazy) {
		this.lazy = lazy;
		this.mapSupplier = lazy ? LAZYMAP_SUPPLIER : HASMAP_SUPPLIER;
		this.listSupplier = lazy ? LAZYLIST_SUPPLIER : ARRAYLIST_SUPPLIER;
		return this;
	}

	// ---------------------------------------------------------------- mappings

	protected Map<Path, Class> mappings;

	/**
	 * Maps a class to JSONs root.
	 */
	public JsonParser map(final Class target) {
		rootType = target;
		return this;
	}

	/**
	 * Maps a class to given path. For arrays, append <code>values</code>
	 * to the path to specify component type (if not specified by
	 * generics).
	 */
	public JsonParser map(final String path, final Class target) {
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
	protected Class replaceWithMappedTypeForPath(final Class target) {
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
	public JsonParser withValueConverter(final String path, final ValueConverter valueConverter) {
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

	protected String classMetadataName = Defaults.classMetadataName;

	/**
	 * Sets local class meta-data name.
	 * <p>
	 * Note that by using the class meta-data name you may expose a security hole in case untrusted source
	 * manages to specify a class that is accessible through class loader and exposes set of methods and/or fields,
	 * access of which opens an actual security hole. Such classes are known as “deserialization gadget”s.
	 *
	 * Because of this, use of "default typing" is not encouraged in general, and in particular is recommended against
	 * if the source of content is not trusted. Conversely, default typing may be used for processing content in
	 * cases where both ends (sender and receiver) are controlled by same entity.
	 */
	public JsonParser setClassMetadataName(final String name) {
		classMetadataName = name;
		return this;
	}

	/**
	 * Sets usage of default class meta-data name.
	 * Using it may introduce a security hole, see {@link #setClassMetadataName(String)} for more details.
	 * @see #setClassMetadataName(String)
	 */
	public JsonParser withClassMetadata(final boolean useMetadata) {
		if (useMetadata) {
			classMetadataName = Defaults.DEFAULT_CLASS_METADATA_NAME;
		}
		else {
			classMetadataName = null;
		}
		return this;
	}

	/**
	 * Adds a {@link jodd.util.Wildcard wildcard} pattern for white-listing classes.
	 * @see #setClassMetadataName(String)
	 */
	public JsonParser allowClass(final String classPattern) {
		if (super.classnameWhitelist == null) {
			super.classnameWhitelist = new ArrayList<>();
		}
		classnameWhitelist.add(classPattern);
		return this;
	}

	/**
	 * Removes the whitelist of allowed classes.
	 * @see #setClassMetadataName(String)
	 */
	public JsonParser allowAllClasses() {
		classnameWhitelist = null;
		return this;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parses input JSON as given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T parse(final String input, final Class<T> targetType) {
		rootType = targetType;
		return _parse(UnsafeUtil.getChars(input));
	}

	/**
	 * Parses input JSON to {@link JsonObject}, special case of {@link #parse(String, Class)}.
	 */
	public JsonObject parseAsJsonObject(final String input) {
		return new JsonObject(parse(input));
	}

	/**
	 * Parses input JSON to {@link JsonArray}, special case of parsing.
	 */
	public JsonArray parseAsJsonArray(final String input) {
		return new JsonArray(parse(input));
	}

	/**
	 * Parses input JSON to a list with specified component type.
	 */
	public <T> List<T> parseAsList(final String string, final Class<T> componentType) {
		return new JsonParser()
			.map(JsonParser.VALUES, componentType)
			.parse(string);
	}

	/**
	 * Parses input JSON to a list with specified key and value types.
	 */
	public <K, V> Map<K, V> parseAsMap(
		final String string, final Class<K> keyType, final Class<V> valueType) {

		return new JsonParser()
			.map(JsonParser.KEYS, keyType)
			.map(JsonParser.VALUES, valueType)
			.parse(string);
	}

	/**
	 * Parses input JSON string.
	 */
	public <T> T parse(final String input) {
		return _parse(UnsafeUtil.getChars(input));
	}

	/**
	 * Parses input JSON as given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T parse(final char[] input, final Class<T> targetType) {
		rootType = targetType;
		return _parse(input);
	}

	/**
	 * Parses input JSON char array.
	 */
	public <T> T parse(final char[] input) {
		return _parse(input);
	}


	private <T> T _parse(final char[] input) {
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

		if (lazy) {
			// lets resolve root lazy values
			value = resolveLazyValue(value);
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
	protected Object parseValue(final Class targetType, final Class keyType, final Class componentType) {
		final ValueConverter valueConverter;

		final char c = input[ndx];

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
				if (lazy) {
					if (notFirstObject) {
						final Object value = new ObjectParser(this, targetType, keyType, componentType);

						skipObject();

						return value;
					}
					else {
						notFirstObject = true;
					}
				}

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


	// ---------------------------------------------------------------- lazy

	/**
	 * Resolves lazy value during the parsing runtime.
	 */
	private Object resolveLazyValue(Object value) {
		if (value instanceof Supplier) {
			value = ((Supplier)value).get();
		}
		return value;
	}

	/**
	 * Skips over complete object. It is not parsed, just skipped. It will be
	 * parsed later, but oonly if required.
	 */
	private void skipObject() {
		int bracketCount = 1;
		boolean insideString = false;

		while (ndx < total) {
			final char c = input[ndx];

			if (insideString) {
				if (c == '\"') {
					insideString = false;
				}
			}
			else {
				if (c == '\"') {
					insideString = true;
				}
				if (c == '{') {
					bracketCount++;
				} else if (c == '}') {
					bracketCount--;
					if (bracketCount == 0) {
						ndx++;
						return;
					}
				}
			}
			ndx++;
		}
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
	 * Parses string content, once when starting quote has been consumed.
	 */
	protected String parseStringContent(final char quote) {
		final int startNdx = ndx;

		// roll-out until the end of the string or the escape char
		while (true) {
			final char c = input[ndx];

			if (c == quote) {
				// no escapes found, just use existing string
				ndx++;
				return new String(input, startNdx, ndx - 1 - startNdx);
			}

			if (c == '\\') {
				break;
			}

			ndx++;
		}

		// escapes found, proceed differently

		textLen = ndx - startNdx;

		growEmpty();

//		for (int i = startNdx, j = 0; j < textLen; i++, j++) {
//			text[j] = input[i];
//		}
		System.arraycopy(input, startNdx, text, 0, textLen);

		// escape char, process everything until the end
		while (true) {
			char c = input[ndx];

			if (c == quote) {
				// done
				ndx++;
				final String str = new String(text, 0, textLen);
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
		final int startNdx = ndx;

		while (true) {
			final char c = input[ndx];

			if (c <= ' ' || CharUtil.equalsOne(c, UNQOUTED_DELIMETERS)) {
				final int currentNdx = ndx;

				// done
				skipWhiteSpaces();

				return new String(input, startNdx, currentNdx - startNdx);
			}

			ndx++;
		}
	}


	// ---------------------------------------------------------------- number

	/**
	 * Parses JSON numbers.
	 */
	protected Number parseNumber() {
		final int startIndex = ndx;

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


		final String value = new String(input, startIndex, ndx - startIndex);

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

	private static boolean isGreaterThenLong(final BigInteger bigInteger) {
		if (bigInteger.compareTo(MAX_LONG) > 0) {
			return true;
		}
		if (bigInteger.compareTo(MIN_LONG) < 0) {
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
		TypeData typeData = null;

		if (targetType != null) {
			targetTypeClassDescriptor = ClassIntrospector.get().lookup(targetType);

			// find if the target is really a map
			// because when classMetadataName != null we are forcing
			// map usage locally in this method

			isTargetRealTypeMap = targetTypeClassDescriptor.isMap();

			typeData = jsonAnnotationManager.lookupTypeData(targetType);
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
			target = mapSupplier.get();
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
				key = jsonAnnotationManager.resolveRealName(targetType, key);
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
						if (lazy) {
							// need to resolve lazy value before injecting objects into it
							value = resolveLazyValue(value);
						}

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
	protected void consume(final char c) {
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
	protected char consumeOneOf(final char c1, final char c2) {
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
	protected final boolean match(final char[] target) {
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
	protected void syntaxError(final String message) {
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

		final CharSequence str = CharArraySequence.of(input, from, to - from);

		throw new JsonException(
				"Syntax error! " + message + "\n" +
				"offset: " + ndx + " near: \"" + left + str + right + "\"");
	}

}