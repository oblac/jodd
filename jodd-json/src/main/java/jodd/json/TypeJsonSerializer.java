// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

/**
 * Type JSON Serializer defines how a type is serialized into JSON string.
 */
public interface TypeJsonSerializer<T> {

	/**
	 * Serializes a value and writes a JSON content.
	 */
	public void serialize(JsonContext jsonContext, T value);

}