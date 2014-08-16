// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.util.StringPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static jodd.util.StringPool.NULL;

/**
 * JSON context used during serialization for building the JSON string.
 */
public class JsonContext {

	// ---------------------------------------------------------------- ctor

	protected final JsonSerializer jsonSerializer;
	protected final Appendable out;
	protected final List<Object> bag;
	protected final Path path;

	public JsonContext(JsonSerializer jsonSerializer, Appendable appendable) {
		this.jsonSerializer = jsonSerializer;
		this.out = appendable;
		this.bag = new ArrayList<Object>();
		this.path = new Path();
	}

	public JsonSerializer getJsonSerializer() {
		return jsonSerializer;
	}

	/**
	 * Returns <code>true</code> if object has been processed during serialization.
	 * Used to prevent circular dependencies. Objects are matched using the identity.
	 */
	public boolean isUsed(Object value) {
		for (int i = 0; i < bag.size(); i++) {
			Object o = bag.get(i);

			if (o == value) {
				return true;
			}
		}

		bag.add(value);

		return false;
	}

	/**
	 * Removes object from current bag, indicating it is not anymore in the path.
	 */
	public void unuseValue() {
		bag.remove(bag.size() - 1);
	}


	/**
	 * Returns current path.
	 */
	public Path getPath() {
		return path;
	}


	// ---------------------------------------------------------------- serializer

	/**
	 * Serializes the object using {@link jodd.json.TypeJsonSerializer type serializer}.
	 */
	public void serialize(Object object) {
		if (object == null) {
			write(NULL);

			return;
		}

		TypeJsonSerializer typeJsonSerializer = null;

		// + read paths map

		if (jsonSerializer.pathSerializersMap != null) {
			typeJsonSerializer = jsonSerializer.pathSerializersMap.get(path);
		}

		Class type = object.getClass();

		// + read types map

		if (jsonSerializer.typeSerializersMap != null) {
			typeJsonSerializer = jsonSerializer.typeSerializersMap.lookup(type);
		}

		// + globals

		if (typeJsonSerializer == null) {
			typeJsonSerializer = JsonSerializer.getDefaultSerializers().lookup(type);
		}

		typeJsonSerializer.serialize(this, object);
	}

	// ---------------------------------------------------------------- push

	protected String pushedName;
	protected boolean pushedComma;

	/**
	 * Stores name to temporary stack. Used when name's value may or may not be
	 * serialized (e.g. it may be excluded), in that case we do not need to
	 * write the name.
	 */
	public void pushName(String name, boolean withComma) {
		pushedName = name;
		pushedComma = withComma;
	}

	/**
	 * Writes stored name to JSON string. Cleans storage.
	 */
	protected void popName() {
		if (pushedName != null) {
			if (pushedComma) {
				writeComma();
			}
			String name = pushedName;
			pushedName = null;
			writeName(name);
		}
	}

	/**
	 * Returns <code>true</code> if {@link #pushName(String, boolean)}  pushed name}
	 * has been {@link #popName() poped, i.e. used}.
	 */
	public boolean isNamePoped() {
		boolean b = pushedName == null;
		pushedName = null;
		return b;
	}

	// ---------------------------------------------------------------- write

	/**
	 * Writes open object sign.
	 */
	public void writeOpenObject() {
		popName();
		write('{');
	}

	/**
	 * Writes close object sign.
	 */
	public void writeCloseObject() {
		write('}');
	}

	/**
	 * Writes object's property name: string and a colon.
	 */
	public void writeName(String name) {
		if (name != null) {
			writeString(name);
		}
		else {
			write(NULL);
		}

		write(':');
	}

	/**
	 * Writes open array sign.
	 */
	public void writeOpenArray() {
		popName();
		write('[');
	}

	/**
	 * Writes close array sign.
	 */
	public void writeCloseArray() {
		write(']');
	}

	/**
	 * Write a quoted and escaped value to the output.
	 */
	public void writeString(String value) {
		popName();

		write(StringPool.QUOTE);

		int len = value.length();

		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);

			switch (c) {
				case '"':
					write("\\\"");
					break;
				case '\\':
					write("\\\\");
					break;
				case '/':
					write("\\/");
					break;
				case '\b':
					write("\\b");
					break;
				case '\f':
					write("\\f");
					break;
				case '\n':
					write("\\n");
					break;
				case '\r':
					write("\\r");
					break;
				case '\t':
					write("\\t");
					break;
				default:
					if (Character.isISOControl(c)) {
						unicode(c);
					}
					else {
						write(c);
					}
			}
		}

		write(StringPool.QUOTE);
	}

	/**
	 * Writes unicode representation of a character.
	 */
	protected void unicode(char c) {
		write("\\u");
		int n = c;
		for (int i = 0; i < 4; ++i) {
			int digit = (n & 0xf000) >> 12;
			write(HEX[digit]);
			n <<= 4;
		}
	}

	/**
	 * Writes comma.
	 */
	public void writeComma() {
		write(',');
	}


	private static final char[] HEX = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	/**
	 * Appends char sequence to the buffer. Used for numbers, nulls, booleans, etc.
	 */
	public void write(CharSequence charSequence) {
		popName();
		try {
			out.append(charSequence);
		} catch (IOException ioex) {
			throw new JsonException(ioex);
		}
	}

	/**
	 * Appends char to the buffer. Used internally.
	 */
	protected void write(char c) {
		try {
			out.append(c);
		} catch (IOException ioex) {
			throw new JsonException(ioex);
		}
	}

	// ---------------------------------------------------------------- matchers

	/**
	 * Matches property types that are ignored by default.
	 */
	public boolean matchIgnoredPropertyTypes(Class propertyType, boolean include) {
		if (include == true) {
			if (propertyType != null && !jsonSerializer.includeCollections) {
				ClassDescriptor propertyTypeClassDescriptor = ClassIntrospector.lookup(propertyType);

				if (propertyTypeClassDescriptor.isCollection()) {
					include = false;
				}
				if (propertyTypeClassDescriptor.isMap()) {
					include = false;
				}
			}
		}
		return include;
	}


	/**
	 * Matched current path to queries. If match is found, provided include
	 * value may be changed.
	 */
	public boolean matchPathToQueries(boolean include) {
		List<PathQuery> pathQueries = jsonSerializer.pathQueries;

		if (pathQueries != null) {
			for (int iteration = 0; iteration < 2; iteration++) {
				for (PathQuery pathQuery : pathQueries) {
					if (iteration == 0 && !pathQuery.isWildcard()) {
						continue;
					}
					if (iteration == 1 && pathQuery.isWildcard()) {
						continue;
					}
					if (pathQuery.matches(path)) {
						include = pathQuery.isIncluded();
					}
				}
			}
		}

		return include;
	}

}