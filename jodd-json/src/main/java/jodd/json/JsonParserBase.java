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
import jodd.introspector.CtorDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.introspector.Setter;
import jodd.typeconverter.TypeConverterManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Just a base class of {@link jodd.json.JsonParser} that contains
 * various utilities, to reduce the size of a parser.
 */
public abstract class JsonParserBase {

	/**
	 * Creates new instance of {@link jodd.json.MapToBean}.
	 */
	protected MapToBean createMapToBean(String classMetadataName) {
		return new MapToBean(this, classMetadataName);
	}

	// ---------------------------------------------------------------- object tools

	/**
	 * Creates new type for JSON array objects.
	 * It returns a collection.
	 * Later, the collection will be converted into the target type.
	 */
	protected Collection<Object> newArrayInstance(Class targetType) {
		if (targetType == null ||
			targetType == List.class ||
			targetType == Collection.class ||
			targetType.isArray()) {

			return new ArrayList<>();
		}

		if (targetType == Set.class) {
			return new HashSet<>();
		}

		try {
			return (Collection<Object>) targetType.newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
	}

	/**
	 * Creates new object or a <code>HashMap</code> if type is not specified.
	 */
	protected Object newObjectInstance(Class targetType) {
		if (targetType == null ||
			targetType == Map.class) {

			return new HashMap();
		}

		ClassDescriptor cd = ClassIntrospector.get().lookup(targetType);

		CtorDescriptor ctorDescriptor = cd.getDefaultCtorDescriptor(true);
		if (ctorDescriptor == null) {
			throw new JsonException("Default ctor not found for: " + targetType.getName());
		}

		try {
			return ctorDescriptor.getConstructor().newInstance();
		} catch (Exception e) {
			throw new JsonException(e);
		}
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
			return TypeConverterManager.get().convertType(value, targetType);
		}
		catch (Exception ex) {
			throw new JsonException("Type conversion failed", ex);
		}
	}

}