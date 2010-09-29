// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package examples.proxetta.dci;

import jodd.proxetta.ProxyAdvice;
import jodd.proxetta.ProxyTarget;

import java.lang.reflect.Field;

/**
 * Advice that creates instances of entities.
 */
public class RoleAdvice implements ProxyAdvice {

	public Object execute() throws Exception {

		// get data about our target
		Object target = ProxyTarget.target();
		Class type = ProxyTarget.targetClass();

		// find field 'self'
		// this is also a good place for performance improvement
		// so we can cache fields to prevent the look up
		Field field = type.getDeclaredField("self");
		field.setAccessible(true);

		// get the entity value
		Object entity = field.get(target);

		if (entity == null) {

			// if there is no value, lets create new entity
			// this entity also may come from some external
			// factory, too.
			entity = field.getType().newInstance();

			// and set the entity value
			field.set(target, entity);
		}
		
		// continue with the method execution
		return ProxyTarget.invoke();
	}
}
