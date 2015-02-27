// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Serializes int arrays.
 */
public class IntArrayJsonSerializer implements TypeJsonSerializer<int[]> {

	public void serialize(JsonContext jsonContext, int[] array) {
		jsonContext.writeOpenArray();

		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				jsonContext.writeComma();
			}
			jsonContext.write(Integer.toString(array[i]));
		}

		jsonContext.writeCloseArray();
	}
}