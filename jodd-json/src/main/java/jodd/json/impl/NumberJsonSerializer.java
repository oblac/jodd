package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

public class NumberJsonSerializer implements TypeJsonSerializer<Number> {

	public void serialize(JsonContext jsonContext, Number value) {
		jsonContext.write(value.toString());
	}

}