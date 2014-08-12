// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json;

/**
 * Value converter for {@link jodd.json.JsonParser}.
 */
public interface ValueConverter<S, T> {

	/**
	 * Converts value from source type to target type.
	 */
	public T convert(S source);

}