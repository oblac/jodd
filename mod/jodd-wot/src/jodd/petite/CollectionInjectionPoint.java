// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.util.ReflectUtil;

import java.lang.reflect.Field;

public class CollectionInjectionPoint {

	public static final CollectionInjectionPoint[] EMPTY = new CollectionInjectionPoint[0];

	public final Field field;

	public final Class targetClass;

	public CollectionInjectionPoint(Field field) {
		this.field = field;
		this.targetClass = ReflectUtil.getComponentType(field.getGenericType());

		if (targetClass == null) {
			throw new PetiteException("Unknown collection injection type for '" +
					field.getDeclaringClass().getSimpleName() + '.' + field.getName() + "'.");
		}
	}
}
