// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.impl;

import jodd.json.JsonContext;
import jodd.json.Path;
import jodd.json.TypeJsonSerializer;
import jodd.util.StringPool;

import java.util.Map;

/**
 * Map serializer.
 */
public class MapJsonSerializer implements TypeJsonSerializer<Map<?, ?>> {

	public void serialize(JsonContext jsonContext, Map<?, ?> map) {
		jsonContext.writeOpenObject();

		int count = 0;

		Path currentPath = jsonContext.getPath();

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			if (count > 0) {
				jsonContext.writeComma();
			}

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

				include = jsonContext.matchIgnoredPropertyTypes(value.getClass(), include);

				// + path queries: excludes/includes

				include = jsonContext.matchPathToQueries(include);
			}

			// done

			if (!include) {
				currentPath.pop();
				continue;
			}

			count++;

			if (key == null) {
				jsonContext.writeName(null);
			} else {
				jsonContext.writeName(key.toString());
			}

			jsonContext.serialize(value);

			currentPath.pop();
		}

		jsonContext.writeCloseObject();
	}
}