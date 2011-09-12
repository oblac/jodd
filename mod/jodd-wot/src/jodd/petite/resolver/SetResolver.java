// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.resolver;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.InjectionPointFactory;
import jodd.petite.SetInjectionPoint;
import jodd.petite.meta.PetiteInject;
import jodd.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves collection fields.
 */
public class SetResolver {

	protected final Map<Class, SetInjectionPoint[]> collections = new HashMap<Class, SetInjectionPoint[]>();

	protected final InjectionPointFactory injectionPointFactory;

	public SetResolver(InjectionPointFactory injectionPointFactory) {
		this.injectionPointFactory = injectionPointFactory;
	}

	/**
	 * Resolves all collections for given type.
	 */
	public SetInjectionPoint[] resolve(Class type, boolean autowire) {
		SetInjectionPoint[] fields = collections.get(type);
		if (fields != null) {
			return fields;
		}

		// lookup fields
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<SetInjectionPoint> list = new ArrayList<SetInjectionPoint>();
		Field[] allFields = cd.getAllFields(true);
		for (Field field : allFields) {
			PetiteInject ref = field.getAnnotation(PetiteInject.class);
			if ((autowire == false) && (ref == null)) {
				continue;
			}

			if (ReflectUtil.isSubclass(field.getType(), Collection.class) == false) {
				continue;
			}

			list.add(injectionPointFactory.createSetInjectionPoint(field));
		}
		if (list.isEmpty()) {
			fields = SetInjectionPoint.EMPTY;
		} else {
			fields = list.toArray(new SetInjectionPoint[list.size()]);
		}
		collections.put(type, fields);
		return fields;
	}

	public void remove(Class type) {
		collections.remove(type);
	}

}
