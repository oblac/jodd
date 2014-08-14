// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Enum serializer.
 */
public class EnumJsonSerializer implements TypeJsonSerializer<Enum>{

	public void serialize(JsonContext jsonContext, Enum value) {
		jsonContext.writeString(value.name());
	}
}