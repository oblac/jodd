// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Fields collection.
 */
class Fields {

	final HashMap<String, Field> fieldsMap;
	Field[] allFields;

	boolean locked;

	Fields(int maxFields) {
		fieldsMap = new HashMap<String, Field>(maxFields);
	}

	void addField(String name, Field field) {
		if (locked == true) {
			throw new IllegalStateException();	// introspection finished
		}
		fieldsMap.put(name, field);
	}

	void lock() {
		locked = true;
		allFields = new Field[fieldsMap.size()];
		int count = 0;
		for (Field field : fieldsMap.values()) {
			allFields[count] = field;
			count++;
		}
	}

	// ---------------------------------------------------------------- get

	Field getField(String name) {
		return fieldsMap.get(name);
	}

	int getCount() {
		return allFields.length;
	}

	Field[] getAllFields() {
		return allFields;
	}

}