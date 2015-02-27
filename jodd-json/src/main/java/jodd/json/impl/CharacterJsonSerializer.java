// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Character serializer.
 */
public class CharacterJsonSerializer implements TypeJsonSerializer<Character> {

	public void serialize(JsonContext jsonContext, Character value) {
		jsonContext.writeString(value.toString());
	}
}