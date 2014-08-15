// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Serializes float arrays.
 */
public class FloatArrayJsonSerializer implements TypeJsonSerializer<float[]> {

	public void serialize(JsonContext jsonContext, float[] array) {
		jsonContext.writeOpenArray();

		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				jsonContext.writeComma();
			}
			jsonContext.write(Float.toString(array[i]));
		}

		jsonContext.writeCloseArray();
	}
}