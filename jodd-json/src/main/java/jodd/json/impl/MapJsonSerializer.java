package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.TypeJsonSerializer;

import java.util.Map;

/**
 * Map serializer.
 */
public class MapJsonSerializer implements TypeJsonSerializer<Map> {

	@Override
	public void serialize(JsonContext jsonContext, Map map) {

		jsonContext.writeOpenObject();

		int count = 0;

		for (Object key : map.keySet()) {

			if (count > 0) {
				jsonContext.writeComma();
			}

			count++;

			if (key == null) {
				jsonContext.writeName(null);
			} else {
				jsonContext.writeName(key.toString());
			}

			Object value = map.get(key);

			jsonContext.serialize(value);
		}


		jsonContext.writeCloseObject();
	}
}