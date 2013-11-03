// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.petite.InjectionPointFactory;
import jodd.petite.SetInjectionPoint;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Resolves collection fields.
 */
public class SetResolver {

	protected final InjectionPointFactory injectionPointFactory;

	public SetResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	/**
	 * Resolves all collections for given type.
	 */
	public SetInjectionPoint[] resolve(Class type, boolean autowire) {
		// lookup fields
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<SetInjectionPoint> list = new ArrayList<SetInjectionPoint>();
		FieldDescriptor[] allFields = cd.getAllFieldDescriptors();

		for (FieldDescriptor fieldDescriptor : allFields) {
			Field field = fieldDescriptor.getField();

			PetiteInject ref = field.getAnnotation(PetiteInject.class);
			if ((autowire == false) && (ref == null)) {
				continue;
			}

			Class fieldType = field.getType();
			if (fieldType != Collection.class && !ReflectUtil.isSubclass(fieldType, Collection.class)) {
				continue;
			}

			list.add(injectionPointFactory.createSetInjectionPoint(field));
		}

		SetInjectionPoint[] fields;

		if (list.isEmpty()) {
			fields = SetInjectionPoint.EMPTY;
		} else {
			fields = list.toArray(new SetInjectionPoint[list.size()]);
		}
		return fields;
	}

}