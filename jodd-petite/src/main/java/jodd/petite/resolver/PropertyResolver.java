// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.petite.InjectionPointFactory;
import jodd.petite.PropertyInjectionPoint;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
	 * Resolves all properties for given type.
	 */
	public PropertyInjectionPoint[] resolve(Class type, boolean autowire) {
		// lookup fields
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<PropertyInjectionPoint> list = new ArrayList<PropertyInjectionPoint>();
		PropertyDescriptor[] allPropertyDescriptors = cd.getAllPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : allPropertyDescriptors) {

			if (propertyDescriptor.isGetterOnly()) {
				continue;
			}

			Class propertyType = propertyDescriptor.getType();
			if (ReflectUtil.isTypeOf(propertyType, Collection.class)) {
				continue;
			}

			MethodDescriptor writeMethodDescriptor = propertyDescriptor.getWriteMethodDescriptor();
			FieldDescriptor fieldDescriptor = propertyDescriptor.getFieldDescriptor();

			PetiteInject ref = null;

			if (writeMethodDescriptor != null) {
				Method method = writeMethodDescriptor.getMethod();

				ref = method.getAnnotation(PetiteInject.class);
			}

			if (ref == null) {
				if (fieldDescriptor != null) {
					Field field = fieldDescriptor.getField();

					ref = field.getAnnotation(PetiteInject.class);
				}
			}

			if ((autowire == false) && (ref == null)) {
				continue;
			}

			String[] refName = null;

			if (ref != null) {
				String name = ref.value().trim();
				if (name.length() != 0) {
					refName = new String[] {name};
				}
			}

			list.add(injectionPointFactory.createPropertyInjectionPoint(propertyDescriptor, refName));
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