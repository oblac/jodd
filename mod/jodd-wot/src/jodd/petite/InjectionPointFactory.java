// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.paramo.Paramo;
import jodd.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
	 * Creates new method injection point.
	 */
	public MethodInjectionPoint createMethodInjectionPoint(Method method, String[][] references) {
		if (references == null || references.length == 0) {
			references = methodDefaultReferences(method);
		}
		if (method.getParameterTypes().length != references.length) {
			throw new PetiteException("Different number of method parameters and references for: '" +
					method.getDeclaringClass().getName() + '#' + method.getName()+ "()'.");
		}
		return new MethodInjectionPoint(method, references);
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
		PetiteReference[] lookupReferences = petiteConfig.getLookupReferences();
		String[] references = new String[lookupReferences.length];

		for (int i = 0; i < references.length; i++) {
			switch (lookupReferences[i]) {
				case NAME:				references[i] = field.getName(); break;
				case TYPE_SHORT_NAME:	references[i] = StringUtil.uncapitalize(field.getType().getSimpleName()); break;
				case TYPE_FULL_NAME:	references[i] = field.getType().getName(); break;
			}
		}
		return references;
	}

	/**
	 * Builds default method references.
	 */
	protected String[][] methodDefaultReferences(Method method) {
		PetiteReference[] lookupReferences = petiteConfig.getLookupReferences();
		Class[] paramTypes = method.getParameterTypes();
		String[] paramNames = null;
		if (petiteConfig.getUseParamo()) {
			paramNames = Paramo.resolveParameterNames(method);
		}

		String[][] references = new String[paramTypes.length][];

		for (int j = 0; j < paramTypes.length; j++) {

			String[] ref = new String[lookupReferences.length];
			references[j] = ref;

			for (int i = 0; i < ref.length; i++) {
				switch (lookupReferences[i]) {
					case NAME:				ref[i] = paramNames != null ? paramNames[j] : null; break;
					case TYPE_SHORT_NAME:	ref[i] = StringUtil.uncapitalize(paramTypes[j].getSimpleName()); break;
					case TYPE_FULL_NAME:	ref[i] = paramTypes[j].getName(); break;
				}
			}
		}

		return references;
	}

}
