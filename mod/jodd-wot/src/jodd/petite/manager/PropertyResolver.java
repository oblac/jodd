// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.manager;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.PropertyInjectionPoint;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves properties.
 */
public class PropertyResolver {

	protected final Map<Class, PropertyInjectionPoint[]> properties = new HashMap<Class, PropertyInjectionPoint[]>();

	/**
	 * Resolves all fields for given type.
	 */
	public PropertyInjectionPoint[] resolve(Class type) {
		PropertyInjectionPoint[] fields = properties.get(type);
		if (fields != null) {
			return fields;
		}

		// lookup fields
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		ArrayList<PropertyInjectionPoint> list = new ArrayList<PropertyInjectionPoint>();
		Field[] allFields = cd.getAllFields(true);
		for (Field field : allFields) {
			PetiteInject ref = field.getAnnotation(PetiteInject.class);
			String refName;
			boolean hasAnnotation;
			if (ref == null) {
				hasAnnotation = false;
				refName = field.getName();
			} else {
				hasAnnotation = true;
				refName = ref.value().trim();
				if (refName.length() == 0) {
					refName = field.getName();
				}
			}
			list.add(new PropertyInjectionPoint(field, refName, hasAnnotation));
		}
		if (list.isEmpty()) {
			fields = PropertyInjectionPoint.EMPTY;
		} else {
			fields = list.toArray(new PropertyInjectionPoint[list.size()]);
		}
		properties.put(type, fields);
		return fields;
	}

}