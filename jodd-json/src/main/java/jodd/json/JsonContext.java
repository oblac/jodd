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
import jodd.util.ClassUtil;
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
	protected final boolean excludeNulls;

	public JsonContext(JsonSerializer jsonSerializer, Appendable appendable, boolean excludeNulls, boolean strictStringEncoding) {
		super(appendable, strictStringEncoding);
		this.jsonSerializer = jsonSerializer;
		this.bag = new ArrayList<>();
		this.path = new Path();
		this.excludeNulls = excludeNulls;
	}

	/**
	 * Returns {@link jodd.json.JsonSerializer}.
	 */
	public JsonSerializer getJsonSerializer() {
		return jsonSerializer;
	}

	/**
	 * Returns <code>true</code> if null values have to be excluded.
	 */
	public boolean isExcludeNulls() {
		return excludeNulls;
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
	 * Returns <code>true</code> if object was written, otherwise returns <code>false</code>.
	 */
	public boolean serialize(Object object) {
		if (object == null) {
			write(NULL);

			return true;
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

		return typeJsonSerializer.serialize(this, object);
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
					if (ClassUtil.isTypeOf(propertyType, excludedType)) {
						return false;
					}
				}
			}
			if (jsonSerializer.excludedTypes != null) {
				for (Class excludedType : jsonSerializer.excludedTypes) {
					if (ClassUtil.isTypeOf(propertyType, excludedType)) {
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