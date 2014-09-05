// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;

/**
 * Iterable serializer.
 */
public class IterableJsonSerializer extends ValueJsonSerializer<Iterable> {

	public void serializeValue(JsonContext jsonContext, Iterable iterable) {
		jsonContext.writeOpenArray();

		int count = 0;
		for (Object element : iterable) {
			if (count > 0) {
				jsonContext.writeComma();
			}
			count++;
			jsonContext.serialize(element);
		}

		jsonContext.writeCloseArray();
	}
}