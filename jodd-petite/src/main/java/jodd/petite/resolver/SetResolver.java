// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.petite.InjectionPointFactory;
import jodd.petite.SetInjectionPoint;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

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
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<SetInjectionPoint> list = new ArrayList<SetInjectionPoint>();

		PropertyDescriptor[] allProperties = cd.getAllPropertyDescriptors();

		for (PropertyDescriptor propertyDescriptor : allProperties) {

			if (propertyDescriptor.isGetterOnly()) {
				continue;
			}

			Class propertyType = propertyDescriptor.getType();
			if (!ReflectUtil.isTypeOf(propertyType, Collection.class)) {
				continue;
			}

			MethodDescriptor writeMethodDescriptor = propertyDescriptor.getWriteMethodDescriptor();
			FieldDescriptor fieldDescriptor = propertyDescriptor.getFieldDescriptor();

			PetiteInject ref = null;

			if (writeMethodDescriptor != null) {
				ref = writeMethodDescriptor.getMethod().getAnnotation(PetiteInject.class);
			}
			if (ref == null && fieldDescriptor != null) {
				ref = fieldDescriptor.getField().getAnnotation(PetiteInject.class);
			}

			if ((autowire == false) && (ref == null)) {
				continue;
			}

			list.add(injectionPointFactory.createSetInjectionPoint(propertyDescriptor));
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