// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.util.ReflectUtil;
import jodd.util.Wildcard;

import java.util.ArrayList;
import java.util.List;

import static jodd.util.StringPool.NULL;

/**
 * JSON context used during serialization for building the JSON string.
 */
public class JsonContext extends JsonWriter {

	// ---------------------------------------------------------------- ctor

	protected final JsonSerializer jsonSerializer;
	protected final List<JsonValueContext> bag;
	protected int bagSize = 0;
	protected final Path path;

	public JsonContext(JsonSerializer jsonSerializer, Appendable appendable) {
		super(appendable);
		this.jsonSerializer = jsonSerializer;
		this.bag = new ArrayList<JsonValueContext>();
		this.path = new Path();
	}

	/**
	 * Returns {@link jodd.json.JsonSerializer}.
	 */
	public JsonSerializer getJsonSerializer() {
		return jsonSerializer;
	}

	// ---------------------------------------------------------------- path and value context

	protected JsonValueContext lastValueContext = null;

	/**
	 * Returns <code>true</code> if object has been already processed during the serialization.
	 * Used to prevent circular dependencies. Objects are matched by identity.
	 */
	public boolean pushValue(Object value) {
		for (int i = 0; i < bagSize; i++) {
			JsonValueContext valueContext = bag.get(i);
			if (valueContext.getValue() == value) {
				return true;
			}
		}

		if (bagSize == bag.size()) {
			lastValueContext = new JsonValueContext(value);
			bag.add(lastValueContext);
		}
		else {
			lastValueContext = bag.get(bagSize);
			lastValueContext.reuse(value);
		}

		bagSize++;

		return false;
	}

	/**
	 * Removes object from current bag, indicating it is not anymore in the path.
	 */
	public void popValue() {
		bagSize--;
		if (bagSize == 0) {
			lastValueContext = null;
		} else {
			lastValueContext = bag.get(bagSize - 1);
		}
	}

	/**
	 * Returns current {@link jodd.json.JsonValueContext value context}.
	 * It may be <code>null</code> if value is not {@link #pushValue(Object) pushed} yet.
	 */
	public JsonValueContext peekValueContext() {
		return lastValueContext;
	}

	/**
	 * Returns current path.
	 */
	public Path getPath() {
		return path;
	}

	// ---------------------------------------------------------------- overwrite

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushName(String name, boolean withComma) {
		JsonValueContext valueContext = peekValueContext();

		if (valueContext != null) {
			valueContext.setPropertyName(name);
		}

		super.pushName(name, withComma);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeComma() {
		JsonValueContext valueContext = peekValueContext();

		if (valueContext != null) {
			valueContext.incrementIndex();
		}

		super.writeComma();
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
			typeJsonSerializer = JoddJson.defaultSerializers.lookup(type);
		}

		typeJsonSerializer.serialize(this, object);
	}

	// ---------------------------------------------------------------- matchers

	/**
	 * Matches property types that are ignored by default.
	 */
	public boolean matchIgnoredPropertyTypes(Class propertyType, boolean excludeMaps, boolean include) {
		if (!include) {
			return false;
		}

		if (propertyType != null) {
			if (!jsonSerializer.deep) {
				ClassDescriptor propertyTypeClassDescriptor = ClassIntrospector.lookup(propertyType);

				if (propertyTypeClassDescriptor.isArray()) {
					return false;
				}
				if (propertyTypeClassDescriptor.isCollection()) {
					return false;
				}
				if (excludeMaps) {
					if (propertyTypeClassDescriptor.isMap()) {
						return false;
					}
				}
			}

			// still not excluded, continue with excluded types and type names

			// + excluded types

			if (JoddJson.excludedTypes != null) {
				for (Class excludedType : JoddJson.excludedTypes) {
					if (ReflectUtil.isTypeOf(propertyType, excludedType)) {
						return false;
					}
				}
			}
			if (jsonSerializer.excludedTypes != null) {
				for (Class excludedType : jsonSerializer.excludedTypes) {
					if (ReflectUtil.isTypeOf(propertyType, excludedType)) {
						return false;
					}
				}
			}

			// + exclude type names

			String propertyTypeName = propertyType.getName();

			if (JoddJson.excludedTypeNames != null) {
				for (String excludedTypeName : JoddJson.excludedTypeNames) {
					if (Wildcard.match(propertyTypeName, excludedTypeName)) {
						return false;
					}
				}
			}
			if (jsonSerializer.excludedTypeNames != null) {
				for (String excludedTypeName : jsonSerializer.excludedTypeNames) {
					if (Wildcard.match(propertyTypeName, excludedTypeName)) {
						return false;
					}
				}
			}
		}

		return true;
	}


	/**
	 * Matched current path to queries. If match is found, provided include
	 * value may be changed.
	 */
	public boolean matchPathToQueries(boolean include) {
		return jsonSerializer.rules.apply(path, include);
	}

}