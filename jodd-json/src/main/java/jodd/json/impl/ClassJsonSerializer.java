// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Class serializer.
 */
public class ClassJsonSerializer implements TypeJsonSerializer<Class> {

	public void serialize(JsonContext jsonContext, Class type) {
		jsonContext.writeString(type.getName());
	}
}