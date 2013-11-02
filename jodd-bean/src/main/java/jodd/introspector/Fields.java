// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Fields collection.
 */
class Fields {

	private final ClassDescriptor classDescriptor;
	private final HashMap<String, FieldDescriptor> fieldsMap;

	// cache
	private FieldDescriptor[] allFields;

	Fields(ClassDescriptor classDescriptor, int maxFields) {
		this.classDescriptor = classDescriptor;
		fieldsMap = new HashMap<String, FieldDescriptor>(maxFields);
	}

	void addField(String name, Field field) {
		fieldsMap.put(name, classDescriptor.createFieldDescriptor(field));

		allFields = null;	// reset cache
	}

	// ---------------------------------------------------------------- get

	/**
	 * Returns {@link FieldDescriptor field descriptor} for given field name
	 * or <code>null</code> if field does not exist.
	 */
	FieldDescriptor getFieldDescriptor(String name) {
		return fieldsMap.get(name);
	}

	/**
	 * Returns all fields of this collection. Returns empty array
	 * if no fields exist. Initialized lazy.
	 */
	FieldDescriptor[] getAllFields() {
		if (allFields == null) {
			FieldDescriptor[] allFields = new FieldDescriptor[fieldsMap.size()];

			int index = 0;
			for (FieldDescriptor fieldDescriptor : fieldsMap.values()) {
				allFields[index] = fieldDescriptor;
				index++;
			}

			this.allFields = allFields;
		}
		return allFields;
	}

}