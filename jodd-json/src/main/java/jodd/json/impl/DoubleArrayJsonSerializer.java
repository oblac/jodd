// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Serializes double arrays.
 */
public class DoubleArrayJsonSerializer implements TypeJsonSerializer<double[]> {

	public void serialize(JsonContext jsonContext, double[] array) {
		jsonContext.writeOpenArray();

		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				jsonContext.writeComma();
			}
			jsonContext.write(Double.toString(array[i]));
		}

		jsonContext.writeCloseArray();
	}
}