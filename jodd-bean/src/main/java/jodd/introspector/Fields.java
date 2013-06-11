// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Fields collection.
 */
class Fields {

	private final HashMap<String, FieldDescriptor> fieldsMap;

	private int count;				// count
	private Field[] allFields;		// cache

	Fields(int maxFields) {
		fieldsMap = new HashMap<String, FieldDescriptor>(maxFields);
		count = 0;
	}

	void addField(String name, Field field, Class implClass) {
		fieldsMap.put(name, new FieldDescriptor(field, implClass));

		// reset cache
		allFields = null;
		// increment count
		count++;
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns field with given name or <code>null</code>
	 * if field not found.
	 */
	Field getField(String name) {
		FieldDescriptor fieldDescriptor = fieldsMap.get(name);

		if (fieldDescriptor == null) {
			return null;
		}

		return fieldDescriptor.getField();
	}

	/**
	 * Returns {@link FieldDescriptor field descriptor}.
	 */
	FieldDescriptor getFieldDescriptor(String name) {
		return fieldsMap.get(name);
	}

	/**
	 * Returns number of fields in this collection.
	 */
	int getCount() {
		return count;
	}

	/**
	 * Returns all fields of this collection. Returns empty array
	 * if no fields exist. Initialized lazy.
	 */
	Field[] getAllFields() {
		if (allFields == null) {
			Field[] allFieldsNew = new Field[fieldsMap.size()];

			int ndx = 0;
			for (FieldDescriptor fieldDescriptor : fieldsMap.values()) {
				allFieldsNew[ndx] = fieldDescriptor.getField();
				ndx++;
			}

			allFields = allFieldsNew;
		}
		return allFields;
	}

}