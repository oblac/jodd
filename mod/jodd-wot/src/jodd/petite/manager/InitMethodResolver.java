// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite.manager;

import jodd.introspector.ClassDescriptor;
import jodd.introspector.ClassIntrospector;
import jodd.petite.meta.PetiteInitMethod;
import jodd.petite.PetiteException;
import jodd.petite.InitMethodPoint;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resolver for bean init methods.
 */
public class InitMethodResolver {

	protected final Map<Class, InitMethodPoint[]> initMethods = new HashMap<Class, InitMethodPoint[]>();

	protected static final InitMethodPoint[] EMPTY_INIT_METHODS = new InitMethodPoint[0];	// saves memory for empty field references

	protected static final class MethodOrder implements Comparable {

		final Method method;
		final int order;

		MethodOrder(Method method, int order) {
			this.method = method;
			this.order = order == 0 ? (Integer.MAX_VALUE >> 1) : (order < 0 ? Integer.MAX_VALUE + order: order);
		}

		public int compareTo(Object other) {
			MethodOrder that = (MethodOrder)other;
			return this.order == that.order ? 0 : (this.order > that.order ? 1 : -1); 
		}
	}

	public InitMethodPoint[] resolve(Object bean) {
		Class<?> type = bean.getClass();
		InitMethodPoint[] methods = initMethods.get(type);
		if (methods != null) {
			return methods;
		}

		// lookup methods
		ClassDescriptor cd = ClassIntrospector.lookup(type);
		List<MethodOrder> list = new ArrayList<MethodOrder>();
		Method[] allMethods = cd.getAllMethods(true);
		for (Method method : allMethods) {
			PetiteInitMethod petiteInitMethod = method.getAnnotation(PetiteInitMethod.class);
			if (petiteInitMethod == null) {
				continue;
			}
			if (method.getParameterTypes().length > 0) {
				throw new PetiteException("Arguments are not allowed for Petite init method: " + type.getName() + '#' + method.getName() + "().");
			}
			int order = petiteInitMethod.order();
			list.add(new MethodOrder(method, order));
		}
		if (list.isEmpty()) {
			methods = EMPTY_INIT_METHODS;
		} else {
			methods = new InitMethodPoint[list.size()];
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				methods[i] = new InitMethodPoint(list.get(i).method);
			}
		}
		initMethods.put(type, methods);
		return methods;
	}

}
