// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Collection of {@link FieldDescriptor field descriptors}.
 */
public class Fields {

	protected final ClassDescriptor classDescriptor;
	protected final HashMap<String, FieldDescriptor> fieldsMap;

	// cache
	private FieldDescriptor[] allFields;

	/**
	 * Creates new fields collection.
	 */
	public Fields(ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
		this.fieldsMap = inspectFields();
	}

	/**
	 * Inspects fields and returns map of {@link FieldDescriptor field descriptors}.
	 */
	protected HashMap<String, FieldDescriptor> inspectFields() {
		boolean scanAccessible = classDescriptor.isScanAccessible();
		Class type = classDescriptor.getType();

		Field[] fields = scanAccessible ? ReflectUtil.getAccessibleFields(type) : ReflectUtil.getSupportedFields(type);

		HashMap<String, FieldDescriptor> map = new HashMap<String, FieldDescriptor>(fields.length);

		for (Field field : fields) {
			String fieldName = field.getName();

			if (fieldName.equals("serialVersionUID")) {
				continue;
			}

			map.put(fieldName, createFieldDescriptor(field));
		}

		return map;
	}

	/**
	 * Creates new {@code FieldDescriptor}.
	 */
	protected FieldDescriptor createFieldDescriptor(Field field) {
		return new FieldDescriptor(classDescriptor, field);
	}


	// ---------------------------------------------------------------- get

	/**
	 * Returns {@link FieldDescriptor field descriptor} for given field name
	 * or <code>null</code> if field does not exist.
	 */
	public FieldDescriptor getFieldDescriptor(String name) {
		return fieldsMap.get(name);
	}

	/**
	 * Returns all fields of this collection. Returns empty array
	 * if no fields exist. Initialized lazy.
	 */
	public FieldDescriptor[] getAllFieldDescriptors() {
		if (allFields == null) {
			FieldDescriptor[] allFields = new FieldDescriptor[fieldsMap.size()];

			int index = 0;
			for (FieldDescriptor fieldDescriptor : fieldsMap.values()) {
				allFields[index] = fieldDescriptor;
				index++;
			}

			Arrays.sort(allFields, new Comparator<FieldDescriptor>() {
				public int compare(FieldDescriptor fd1, FieldDescriptor fd2) {
					return fd1.getField().getName().compareTo(fd2.getField().getName());
				}
			});

			this.allFields = allFields;
		}
		return allFields;
	}

}