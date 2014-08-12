package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Boolean serializer.
 */
public class BooleanJsonSerializer implements TypeJsonSerializer<Boolean> {

	@Override
	public void serialize(JsonContext jsonContext, Boolean value) {
		jsonContext.write(value.toString());
	}
}