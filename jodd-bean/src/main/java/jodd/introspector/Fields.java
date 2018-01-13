// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.introspector;

import jodd.util.ClassUtil;

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
	public Fields(final ClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
		this.fieldsMap = inspectFields();
	}

	/**
	 * Inspects fields and returns map of {@link FieldDescriptor field descriptors}.
	 */
	protected HashMap<String, FieldDescriptor> inspectFields() {
		boolean scanAccessible = classDescriptor.isScanAccessible();
		Class type = classDescriptor.getType();

		Field[] fields = scanAccessible ? ClassUtil.getAccessibleFields(type) : ClassUtil.getSupportedFields(type);

		HashMap<String, FieldDescriptor> map = new HashMap<>(fields.length);

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
	protected FieldDescriptor createFieldDescriptor(final Field field) {
		return new FieldDescriptor(classDescriptor, field);
	}


	// ---------------------------------------------------------------- get

	/**
	 * Returns {@link FieldDescriptor field descriptor} for given field name
	 * or <code>null</code> if field does not exist.
	 */
	public FieldDescriptor getFieldDescriptor(final String name) {
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
				public int compare(final FieldDescriptor fd1, final FieldDescriptor fd2) {
					return fd1.getField().getName().compareTo(fd2.getField().getName());
				}
			});

			this.allFields = allFields;
		}
		return allFields;
	}

}