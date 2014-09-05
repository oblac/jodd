// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Base class for all JSON objects and arrays serializers.
 * Detects circular dependencies and pushes value to current type
 * context of provided json context.
 */
public abstract class ValueJsonSerializer<T> implements TypeJsonSerializer<T> {

	/**
	 * Detects circular dependencies and pushes value as current
	 * type context.
	 */
	public final void serialize(JsonContext jsonContext, T value) {
		if (jsonContext.pushValue(value)) {
			// prevent circular dependencies
			return;
		}

		serializeValue(jsonContext, value);

		jsonContext.popValue();
	}

	/**
	 * Performs the serialization of the value.
	 */
	public abstract void serializeValue(JsonContext jsonContext, T value);
}