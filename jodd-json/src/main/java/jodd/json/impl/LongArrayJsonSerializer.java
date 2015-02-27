// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Serializes long arrays.
 */
public class LongArrayJsonSerializer implements TypeJsonSerializer<long[]> {

	public void serialize(JsonContext jsonContext, long[] array) {
		jsonContext.writeOpenArray();

		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				jsonContext.writeComma();
			}
			jsonContext.write(Long.toString(array[i]));
		}

		jsonContext.writeCloseArray();
	}
}