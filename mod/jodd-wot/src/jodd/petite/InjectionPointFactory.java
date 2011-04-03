// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import java.lang.reflect.Field;

public class InjectionPointFactory {

	protected final PetiteConfig petiteConfig;

	public InjectionPointFactory(PetiteConfig petiteConfig) {
		this.petiteConfig = petiteConfig;
	}

	/**
	 * Creates new property injection point.
	 */
	public PropertyInjectionPoint createPropertyInjectionPoint(Field field, String[] references) {
		if (references == null || references.length == 0) {
			references = PetiteUtil.fieldDefaultReferences(field);
		}
		return new PropertyInjectionPoint(field, references);
	}
}
