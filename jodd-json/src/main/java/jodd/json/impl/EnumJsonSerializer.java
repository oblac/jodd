package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Enum serializer.
 */
public class EnumJsonSerializer implements TypeJsonSerializer<Enum>{

	@Override
	public void serialize(JsonContext jsonContext, Enum value) {
		jsonContext.writeString(value.name());
	}
}