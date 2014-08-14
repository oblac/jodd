// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Boolean serializer.
 */
public class BooleanJsonSerializer implements TypeJsonSerializer<Boolean> {

	public void serialize(JsonContext jsonContext, Boolean value) {
		jsonContext.write(value.toString());
	}
}