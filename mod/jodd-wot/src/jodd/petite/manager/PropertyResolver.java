// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.manager;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.PetiteUtil;
import jodd.petite.PropertyInjectionPoint;
import jodd.petite.meta.PetiteInject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Resolves properties.
 */
public class PropertyResolver {

	protected final Map<Class, PropertyInjectionPoint[]> properties = new HashMap<Class, PropertyInjectionPoint[]>();

	/**
	 * Resolves all fields for given type.
	 */
	public PropertyInjectionPoint[] resolve(Class type) {		// todo pass wire information
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
			String[] refName = null;
			boolean hasAnnotation = false;

			if (ref != null) {
				hasAnnotation = true;

				String name = ref.value().trim();
				if (name.length() != 0) {
					refName = new String[] {name};
				}
			}

			if (refName == null) {
				refName = PetiteUtil.fieldDefaultReferences(field);
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