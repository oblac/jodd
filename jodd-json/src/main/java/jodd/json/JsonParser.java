// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.Getter;
import jodd.introspector.PropertyDescriptor;
import jodd.introspector.Setter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.CharUtil;
import jodd.util.UnsafeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple, developer-friendly JSON parser.
 */
public class JsonParser {

	private static final char[] T_RUE = new char[] {'r', 'u', 'e'};
	private static final char[] F_ALSE = new char[] {'a', 'l', 's', 'e'};

	protected int ndx = 0;
	protected char[] input;
	protected int total;
	protected Path path;

	public JsonParser() {
		text = new char[512];
	}

	/**
	 * Resets parser, so it can be reused.
	 */
	protected void reset() {
		this.ndx = 0;
		this.textLen = 0;
		this.path = new Path();
	}

	// ---------------------------------------------------------------- use

	protected Map<Path, Class> uses;

	/**
	 * Uses class for JSONs root.
	 */
	public JsonParser use(Class target) {
		return use(null, target);
	}

	/**
	 * Uses class for given path. For arrays, append <code>values</code>
	 * to the path to specify component type.
	 */
	public JsonParser use(String path, Class target) {
		if (uses == null) {
			uses = new HashMap<Path, Class>();
		}
		uses.put(Path.parse(path), target);
		return this;
	}

	/**
	 * Replaces type with specified type from the specified uses for current path.
	 */
	protected Class replaceWithPathType(Class target) {
		if (uses == null) {
			return target;
		}

		Class newType = uses.get(path);

		if (newType == null) {
			return target;
		}

		return newType;
	}

	// ---------------------------------------------------------------- parse

	/**
	 * Parses input JSON as given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T parse(String input, Class<T> targetType) {
		use(targetType);
		return (T) parse(input);
	}

	/**
	 * Parses input JSON string.
	 */
	public Object parse(String input) {
		char[] chars = UnsafeUtil.getChars(input);
		return parse(chars);
	}

	/**
	 * Parses input JSON as given type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T parse(char[] input, Class<T> targetType) {
		use(targetType);
		return (T) parse(input);
	}

	/**
	 * Parses input JSON char array.
	 */
	public Object parse(char[] input) {
		this.input = input;
		this.total = input.length;

		reset();

		skipWhiteSpaces();

		Class targetType = replaceWithPathType(null);

		Object value = parseValue(targetType, null);

		skipWhiteSpaces();

		if (ndx != total) {
			// must be at the end
			syntaxError("");
		}

		return value;
	}

	// ---------------------------------------------------------------- parser

	/**
	 * Parses a JSON value.
	 * @param targetType target type to convert, may be <code>null</code>
	 * @param componentType component type for arrays, may be <code>null</code>
	 */
	protected Object parseValue(Class targetType, Class componentType) {

		if (isEOF()) {
			syntaxErrorEOF();
		}

		// todo change signature to <T> for targetType
		char c = input[ndx];

		switch (c) {
			case '"':
				ndx++;
				String string =  parseStringContent();

				if (targetType != null) {
					return convertType(string, targetType);
				}
				return string;

			case '{':
				ndx++;
				return parseObjectContent(targetType);

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
				Number number = parseNumber();

				if (targetType != null) {
					return convertType(number, targetType);
				}
				return number;

			case 't':
				ndx++;
				if (match(T_RUE, ndx)) {
					ndx += 3;

					if (targetType != null) {
						return convertType(Boolean.TRUE, targetType);
					}
					return Boolean.TRUE;
				}
				break;

			case 'f':
				ndx++;
				if (match(F_ALSE, ndx)) {
					ndx += 4;

					if (targetType != null) {
						return convertType(Boolean.FALSE, targetType);
					}
					return Boolean.FALSE;
				}
				break;
		}

		syntaxError("Invalid char: " + c);
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
		while (true) {
			char c = input[ndx];

			if (c == '\"') {
				ndx++;
				return emitText();
			}

			if (c == '\\') {
				// escape
				ndx++;
				c = input[ndx];

				switch (c) {
					case '\"' : emitChar('\"'); break;
					case '\\' : emitChar('\\'); break;
					case '/' : emitChar('/'); break;
					case 'b' : emitChar('\b'); break;
					case 'f' : emitChar('\f'); break;
					case 'n' : emitChar('\n'); break;
					case 'r' : emitChar('\r'); break;
					case 't' : emitChar('\t'); break;
					case 'u' : //
					default:
						// todo error?
				}
			}

			emitChar(c);
			ndx++;
		}
	}


	/**
	 * Appends single char to the text buffer.
	 */
	protected void emitChar(char c) {
		if (textLen == text.length) {
			// ensure size
			int newSize = textLen + textLen >> 1;

			char[] newText = new char[newSize];
			System.arraycopy(text, 0, newText, 0, text.length);

			text = newText;
		}
		text[textLen++] = c;
	}

	/**
	 * Emits parsed text.
	 */
	protected String emitText() {
		String s = new String(text, 0, textLen);

		textLen = 0;

		return s;
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

			if (CharUtil.isDigit(c)) {
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
			longNumber = Long.parseLong(value);
		}

		if ((longNumber > 0 && longNumber <= Integer.MAX_VALUE) || (longNumber < 0 && longNumber >= Integer.MIN_VALUE)) {
			return Integer.valueOf((int) longNumber);
		}
		return Long.valueOf(longNumber);
	}

	// ---------------------------------------------------------------- array

	/**
	 * Parses arrays, once when open bracket has been consumed.
	 */
	protected Object parseArrayContent(Class targetType, Class componentType) {
		targetType = replaceWithPathType(targetType);

		path.push("values");
		componentType = replaceWithPathType(componentType);
		path.pop();

		Object target = newArrayInstance(targetType);

		ClassDescriptor cd = ClassIntrospector.lookup(target.getClass());

		boolean isList = cd.isList();

		mainloop:
		while (true) {
			skipWhiteSpaces();

			Object value = parseValue(componentType, null);

			if (componentType != null) {
				value = convertType(value, componentType);
			}

			if (isList) {
				((List) target).add(value);
			}

			skipWhiteSpaces();

			if (isEOF()) {
				syntaxErrorEOF();
			}

			char c = input[ndx];

			switch (c) {
				case ']': ndx++; break mainloop;
				case ',': ndx++; break;
				default: syntaxError("Invalid char, expected ] or ,");
			}

		}

		return target;
	}

	// ---------------------------------------------------------------- object

	/**
	 * Parses object, once when open bracket has been consumed.
	 */
	protected Object parseObjectContent(Class targetType) {
		targetType = replaceWithPathType(targetType);

		Object target = newObjectInstance(targetType);

		mainloop:
		while (true) {
			skipWhiteSpaces();

			String key = parseString();

			skipWhiteSpaces();

			consume(':');

			skipWhiteSpaces();

			// read the type of the simple property

			PropertyDescriptor pd = null;
			Class propertyType = null;
			Class componentType = null;

			if (targetType != null) {
				pd = resolveSimpleProperty(targetType, key);

				if (pd != null) {
					propertyType = pd.getType();

					componentType = resolveComponentType(pd);
				}
			}

			path.push(key);

			Object value = parseValue(propertyType, componentType);

			path.pop();

			skipWhiteSpaces();

			// inject
			setDeclaredPropertyForced(target, pd, key, value);

			if (isEOF()) {
				syntaxErrorEOF();
			}

			char c = input[ndx];

			switch (c) {
				case '}': ndx++; break mainloop;
				case ',': ndx++; break;
				default: syntaxError("Invalid char, expected } or ,");
			}

		}
		return target;
	}

	// ---------------------------------------------------------------- scanning tools

	/**
	 * Consumes char at current position. If char is different, throws the exception.
	 */
	protected void consume(char c) {
		if (isEOF()) {
			syntaxErrorEOF();
		}

		if (input[ndx] != c) {
			syntaxError("Invalid char. expected " + c);
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
	protected final boolean match(char[] target, int ndx) {
		if (ndx + target.length > total) {
			return false;
		}

		int j = ndx;

		for (int i = 0; i < target.length; i++, j++) {
			if (input[j] != target[i]) {
				return false;
			}
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
		try {
			return targetType.newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Creates new type for JSON array objects.
	 */
	protected Object newArrayInstance(Class targetType) {
		if (targetType == null) {
			return new ArrayList();
		}

	    if (targetType == List.class) {
			return new ArrayList();
		}

		try {
			return targetType.newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Resolves property type for given object.
	 * Returns <code>null</code> when property type is unknown (either property is missing
	 * or it is a map).
	 */
	protected PropertyDescriptor resolveSimpleProperty(Class type, String key) {
		if (type == null) {
			return null;
		}

		ClassDescriptor cd = ClassIntrospector.lookup(type);

		if (cd.isMap()) {
			return null;
		}

		return cd.getPropertyDescriptor(key, true);
	}

	/**
	 * Resolves component type for given property descriptor.
	 */
	protected Class resolveComponentType(PropertyDescriptor pd) {
		Class componentType = null;

		Getter getter = pd.getGetter(true);

		if (getter != null) {
			componentType = getter.getGetterRawKeyComponentType();
		}

		if (componentType == null) {
			FieldDescriptor fieldDescriptor = pd.getFieldDescriptor();

			if (fieldDescriptor != null) {
				componentType = fieldDescriptor.getRawKeyComponentType();
			}
		}

		return componentType;
	}

	/**
	 * Injects value into the target property.
	 */
	protected void setDeclaredPropertyForced(Object target, PropertyDescriptor pd, String key, Object value) {
		if (pd == null) {
			((Map) target).put(key, value);
			return;
		}

		Class targetClass = pd.getType();

		Object convertedValue = convertType(value, targetClass);

		try {
			Setter setter = pd.getSetter(true);
			if (setter != null) {
				setter.invokeSetter(target, convertedValue);
			}
			else {
				FieldDescriptor fd = pd.getFieldDescriptor();
				fd.getField().set(target, convertedValue);
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

	protected void syntaxErrorEOF() {
		syntaxError("End of JSON reached");
	}

	/**
	 * Throws {@link jodd.json.JsonException} indicating a syntax error.
	 */
	protected void syntaxError(String message) {
		int from = ndx - 5;
		if (from < 0) {
			from = 0;
		}

		int to = ndx + 5;
		if (to > input.length) {
			to = input.length;
		}

		String str = String.valueOf(input, from, to - from);

		throw new JsonException("Syntax error: " + message + "\noffset: " + ndx + " near: \"..." + str + "...\"");
	}

}