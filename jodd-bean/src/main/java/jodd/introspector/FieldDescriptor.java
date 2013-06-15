//  Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.introspector;

import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Field descriptor. Holds additional field data,
 * that might be specific to implementation class.
 */
public class FieldDescriptor {

	protected final Field field;
	protected final Class rawType;
	protected final Class rawComponentType;

	public FieldDescriptor(Field field, Class implClass) {
		this.field = field;
		Type type = field.getGenericType();
		this.rawType = ReflectUtil.getRawType(type, implClass);
		this.rawComponentType = ReflectUtil.getComponentType(type, implClass);
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

	/**
	 * Returns fields raw component type. Returns <code>null</code>
	 * if field has no component type.
	 */
	public Class getRawComponentType() {
		return rawComponentType;
	}

}