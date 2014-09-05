// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;

import java.lang.reflect.Array;

/**
 * Arrays serializer. May be overridden for specific types for better performances.
 */
public class ArraysJsonSerializer<K> extends ValueJsonSerializer<Object> {

	/**
	 * Returns array's length.
	 */
	protected int getLength(K[] array) {
		return Array.getLength(array);
	}

	/**
	 * Returns array's element at given index.
	 */
	protected K get(K[] array, int index) {
		return (K) Array.get(array, index);
	}

	public void serializeValue(JsonContext jsonContext, Object array) {
		jsonContext.writeOpenArray();

		int length = getLength((K[]) array);

		for (int i = 0; i < length; i++) {
			if (i > 0) {
				jsonContext.writeComma();
			}

			jsonContext.serialize(get((K[]) array, i));
		}

		jsonContext.writeCloseArray();
	}
}