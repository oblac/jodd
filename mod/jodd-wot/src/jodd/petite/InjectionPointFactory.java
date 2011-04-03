// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.util.StringUtil;

import java.lang.reflect.Field;

/**
 * Factory for injection points. Responsible also for
 * resolving default references when none specified.
 */
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
			references = fieldDefaultReferences(field);
		}
		return new PropertyInjectionPoint(field, references);
	}

	// ---------------------------------------------------------------- utils

	/**
	 * Builds default field references.
	 */
	protected String[] fieldDefaultReferences(Field field) {
		String[] references = new String[3];
		references[0] = field.getName();
		references[1] = StringUtil.uncapitalize(field.getType().getSimpleName());
		references[2] = field.getType().getName();
		return references;
	}

}
