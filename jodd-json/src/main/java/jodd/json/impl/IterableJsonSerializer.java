package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

/**
 * Iterable serializer.
 */
public class IterableJsonSerializer implements TypeJsonSerializer<Iterable> {

	@Override
	public void serialize(JsonContext jsonContext, Iterable iterable) {
		jsonContext.writeOpenArray();

		int count = 0;
		for (Object element : iterable) {
			if (count > 0) {
				jsonContext.writeComma();
			}
			count++;
			jsonContext.serialize(element);
		}

		jsonContext.writeCloseArray();
	}
}