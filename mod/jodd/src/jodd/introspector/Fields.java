// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Fields map collection.
 */
class Fields {

	HashMap<String, Field> fMap = new HashMap<String, Field>();
	Field[] allFields;
	boolean locked;

	void addField(String name, Field field) {
		if (locked == true) {
			throw new IllegalStateException("Fields introspection is already finished.");
		}
		fMap.put(name, field);
	}

	void lock() {
		locked = true;
		allFields = new Field[fMap.size()];
		int count = 0;
		for (Field field : fMap.values()) {
			allFields[count] = field;
			count++;
		}
	}

	// ---------------------------------------------------------------- get

	Field getField(String name) {
		return fMap.get(name);
	}

	int getCount() {
		return allFields.length;
	}

	Field[] getAllFields() {
		return allFields;
	}

}