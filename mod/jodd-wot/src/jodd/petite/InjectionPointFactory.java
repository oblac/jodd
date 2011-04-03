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
		PetiteReference[] referencesToUse = petiteConfig.getDefaultReferences();
		String[] references = new String[referencesToUse.length];

		for (int i = 0; i < references.length; i++) {
			switch (referencesToUse[i]) {
				case NAME:				references[i] = field.getName(); break;
				case TYPE_SHORT_NAME:	references[i] = StringUtil.uncapitalize(field.getType().getSimpleName()); break;
				case TYPE_FULL_NAME:	references[i] = field.getType().getName(); break;
			}
		}
		return references;
	}

}
