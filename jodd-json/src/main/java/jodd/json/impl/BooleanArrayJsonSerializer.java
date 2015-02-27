// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Serializes boolean arrays.
 */
public class BooleanArrayJsonSerializer implements TypeJsonSerializer<boolean[]> {

	public void serialize(JsonContext jsonContext, boolean[] array) {
		jsonContext.writeOpenArray();

		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				jsonContext.writeComma();
			}
			jsonContext.write(Boolean.toString(array[i]));
		}

		jsonContext.writeCloseArray();
	}

}