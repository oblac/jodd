// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.InjectionPointFactory;
import jodd.petite.PropertyInjectionPoint;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Resolves properties.
 */
public class PropertyResolver {

	protected final Map<Class, PropertyInjectionPoint[]> properties = new HashMap<Class, PropertyInjectionPoint[]>();

	protected final InjectionPointFactory injectionPointFactory;

	public PropertyResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	/**
	 * Resolves all fields for given type.
	 */
	public PropertyInjectionPoint[] resolve(Class type, boolean autowire) {
		PropertyInjectionPoint[] fields = properties.get(type);
		if (fields != null) {
			return fields;
		}

		// lookup fields
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<PropertyInjectionPoint> list = new ArrayList<PropertyInjectionPoint>();
		Field[] allFields = cd.getAllFields(true);
		for (Field field : allFields) {
			PetiteInject ref = field.getAnnotation(PetiteInject.class);
			if ((autowire == false) && (ref == null)) {
				continue;
			}

			if (ReflectUtil.isSubclass(field.getType(), Collection.class)) {
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
		if (list.isEmpty()) {
			fields = PropertyInjectionPoint.EMPTY;
		} else {
			fields = list.toArray(new PropertyInjectionPoint[list.size()]);
		}
		properties.put(type, fields);
		return fields;
	}

	public void remove(Class type) {
		properties.remove(type);
	}

}