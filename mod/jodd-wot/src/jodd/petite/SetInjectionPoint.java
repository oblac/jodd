// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Set injection point.
 */
public class SetInjectionPoint<T> {

	public static final SetInjectionPoint[] EMPTY = new SetInjectionPoint[0];

	public final Field field;

	public final Class<T> type;

	public final Class targetClass;

	public SetInjectionPoint(Field field) {
		this.field = field;
		this.type = resolveSetType(field);

		this.targetClass = ReflectUtil.getComponentType(field.getGenericType());
		if (targetClass == null) {
			throw new PetiteException("Unknown Petite set component type '" +
					field.getDeclaringClass().getSimpleName() + '.' + field.getName() + "'.");
		}
	}

	@SuppressWarnings({"unchecked"})
	protected Class<T> resolveSetType(Field field) {
		Class<T> type = (Class<T>) field.getType();

		if (type == Collection.class ||
				type == Set.class ||
				type == HashSet.class) {
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
