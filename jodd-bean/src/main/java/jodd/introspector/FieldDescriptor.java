//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Field;

/**
 * Field descriptor.
 */
public class FieldDescriptor {

	protected final Field field;
	protected final Class rawType;

	public FieldDescriptor(Field field, Class implClass) {
		this.field = field;
		this.rawType = ReflectUtil.getRawType(field.getGenericType(), implClass);
	}

	/**
	 * Returns field.
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Returns fields raw type.
	 */
	public Class getRawType() {
		return rawType;
	}

}