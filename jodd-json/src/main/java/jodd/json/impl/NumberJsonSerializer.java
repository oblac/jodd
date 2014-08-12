// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

public class NumberJsonSerializer implements TypeJsonSerializer<Number> {

	public void serialize(JsonContext jsonContext, Number value) {
		jsonContext.write(value.toString());
	}

}