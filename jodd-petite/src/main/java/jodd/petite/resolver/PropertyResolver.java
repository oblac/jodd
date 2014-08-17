// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.petite.InjectionPointFactory;
import jodd.petite.PropertyInjectionPoint;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Resolves properties.
 */
public class PropertyResolver {

	protected final InjectionPointFactory injectionPointFactory;

	public PropertyResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	/**
	 * Resolves all fields for given type.
	 */
	public PropertyInjectionPoint[] resolve(Class type, boolean autowire) {
		// lookup fields
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<PropertyInjectionPoint> list = new ArrayList<PropertyInjectionPoint>();
		FieldDescriptor[] allFields = cd.getAllFieldDescriptors();

		for (FieldDescriptor fieldDescriptor : allFields) {
			Field field = fieldDescriptor.getField();

			PetiteInject ref = field.getAnnotation(PetiteInject.class);
			if ((autowire == false) && (ref == null)) {
				continue;
			}

			Class fieldType = field.getType();
			if (ReflectUtil.isClassOf(fieldType, Collection.class)) {
				continue;
			}

			String[] refName = null;

			if (ref != null) {
				String name = ref.value().trim();
				if (name.length() != 0) {
					refName = new String[] {name};
				}
			}

			list.add(injectionPointFactory.createPropertyInjectionPoint(field, refName));
		}

		PropertyInjectionPoint[] fields;

		if (list.isEmpty()) {
			fields = PropertyInjectionPoint.EMPTY;
		} else {
			fields = list.toArray(new PropertyInjectionPoint[list.size()]);
		}

		return fields;
	}

}