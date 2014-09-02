// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.util.ReflectUtil;

import java.util.Collection;
import java.util.HashSet;

/**
 * Set injection point.
 */
public class SetInjectionPoint<T> {

	public static final SetInjectionPoint[] EMPTY = new SetInjectionPoint[0];

	public final PropertyDescriptor propertyDescriptor;

	public final Class<T> type;

	public final Class targetClass;

	public SetInjectionPoint(PropertyDescriptor propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
		this.type = resolveSetType(propertyDescriptor);

		// resolve component type
		Class targetClass = null;

		MethodDescriptor writeMethodDescriptor = propertyDescriptor.getWriteMethodDescriptor();
		FieldDescriptor fieldDescriptor = propertyDescriptor.getFieldDescriptor();

		if (writeMethodDescriptor != null) {
			targetClass = writeMethodDescriptor.getSetterRawComponentType();
		}
		if (targetClass == null && fieldDescriptor != null) {
			targetClass = fieldDescriptor.getRawComponentType();
		}

		this.targetClass = targetClass;

		if (targetClass == null) {
			throw new PetiteException("Unknown Petite set component type " +
					type.getSimpleName() + '.' + propertyDescriptor.getName());
		}
	}

	@SuppressWarnings({"unchecked"})
	protected Class<T> resolveSetType(PropertyDescriptor propertyDescriptor) {
		Class<T> type = (Class<T>) propertyDescriptor.getType();

		if (ReflectUtil.isTypeOf(type, Collection.class)) {
			return type;
		}
		throw new PetiteException("Unsupported Petite set type: " + type.getName());
	}

	/**
	 * Creates target set for injection. For now it creates <code>HashSet</code>,
	 * but custom implementation can change this setting.
	 */
	public Collection<T> createSet(int length) {
		return new HashSet<T>(length);
	}
}
