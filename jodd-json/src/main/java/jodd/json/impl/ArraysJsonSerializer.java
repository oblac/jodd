// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

import java.lang.reflect.Array;

/**
 * Arrays serializer.
 */
public class ArraysJsonSerializer implements TypeJsonSerializer<Object> {

	public void serialize(JsonContext jsonContext, Object array) {
		jsonContext.writeOpenArray();

		Class type = array.getClass();

		if (type == int[].class) {
			int[] ints = (int[]) array;
			for (int i = 0; i < ints.length; i++) {
				if (i > 0) {
					jsonContext.writeComma();
				}
				jsonContext.write(Integer.toString(ints[i]));
			}
		}
		else if (type == long[].class) {
			long[] longs = (long[]) array;
			for (int i = 0; i < longs.length; i++) {
				if (i > 0) {
					jsonContext.writeComma();
				}
				jsonContext.write(Long.toString(longs[i]));
			}
		}
		else if (type == double[].class) {
			double[] doubles = (double[]) array;
			for (int i = 0; i < doubles.length; i++) {
				if (i > 0) {
					jsonContext.writeComma();
				}
				jsonContext.write(Double.toString(doubles[i]));
			}
		}
		else {
			int length = Array.getLength(array);
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					jsonContext.writeComma();
				}
				jsonContext.serialize(Array.get(array, i));
			}
		}

		jsonContext.writeCloseArray();
	}
}