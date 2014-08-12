// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * CharSequence serializers.
 */
public class CharSequenceJsonSerializer implements TypeJsonSerializer<CharSequence> {

	public void serialize(JsonContext jsonContext, CharSequence value) {
		jsonContext.writeString(value.toString());
	}
}