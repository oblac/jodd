// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.Path;
import jodd.util.StringPool;

import java.util.Map;

/**
 * Map serializer.
 */
public class MapJsonSerializer extends ValueJsonSerializer<Map<?, ?>> {

	public void serializeValue(JsonContext jsonContext, Map<?, ?> map) {
		jsonContext.writeOpenObject();

		int count = 0;

		Path currentPath = jsonContext.getPath();

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			final Object key = entry.getKey();
			final Object value = entry.getValue();

			if (key != null) {
				currentPath.push(key.toString());
			} else {
				currentPath.push(StringPool.NULL);
			}

			// check if we should include the field

			boolean include = true;

			if (value != null) {

				// + all collections are not serialized by default

				include = jsonContext.matchIgnoredPropertyTypes(value.getClass(), false, include);

				// + path queries: excludes/includes

				include = jsonContext.matchPathToQueries(include);
			}

			// done

			if (!include) {
				currentPath.pop();
				continue;
			}

			if (key == null) {
				jsonContext.pushName(null, count > 0);
			} else {
				jsonContext.pushName(key.toString(), count > 0);
			}

			jsonContext.serialize(value);

			if (jsonContext.isNamePopped()) {
				count++;
			}

			currentPath.pop();
		}

		jsonContext.writeCloseObject();
	}
}