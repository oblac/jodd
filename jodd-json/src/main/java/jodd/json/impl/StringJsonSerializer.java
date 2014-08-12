package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * String serializers.
 */
public class StringJsonSerializer implements TypeJsonSerializer<String> {

	public void serialize(JsonContext jsonContext, String value) {
		jsonContext.writeString(value);
	}
}