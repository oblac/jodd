// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Serializes byte arrays.
 */
public class ByteArrayJsonSerializer implements TypeJsonSerializer<byte[]> {

	public void serialize(JsonContext jsonContext, byte[] array) {
		jsonContext.writeOpenArray();

		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				jsonContext.writeComma();
			}
			jsonContext.write(Byte.toString(array[i]));
		}

		jsonContext.writeCloseArray();
	}
}