// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.util.StringUtil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
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
	 * Creates new ctor injection point.
	 */
	public CtorInjectionPoint createCtorInjectionPoint(Constructor constructor, String[][] references) {
		if (references == null || references.length == 0) {
			references = methodOrCtorDefaultReferences(constructor, constructor.getParameterTypes());
		}
		if (constructor.getParameterTypes().length != references.length) {
			throw new PetiteException("Different number of constructor parameters and references for: '"
					+ constructor.getName() + "'.");
		}
		removeDuplicateNames(references);
		return new CtorInjectionPoint(constructor, references);
	}

	/**
	 * Creates new method injection point.
	 */
	public MethodInjectionPoint createMethodInjectionPoint(Method method, String[][] references) {
		if (references == null || references.length == 0) {
			references = methodOrCtorDefaultReferences(method, method.getParameterTypes());
		}
		if (method.getParameterTypes().length != references.length) {
			throw new PetiteException("Different number of method parameters and references for: '" +
					method.getDeclaringClass().getName() + '#' + method.getName()+ "()'.");
		}
		removeDuplicateNames(references);
		return new MethodInjectionPoint(method, references);
	}

	/**
	 * Creates new property injection point.
	 */
	public PropertyInjectionPoint createPropertyInjectionPoint(Field field, String[] references) {
		if (references == null || references.length == 0) {
			references = fieldDefaultReferences(field);
		}
		removeDuplicateNames(references);
		return new PropertyInjectionPoint(field, references);
	}

	/**
	 * Creates new set injection point.
	 */
	public SetInjectionPoint createSetInjectionPoint(Field field) {
		return new SetInjectionPoint(field);
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
	protected String[][] methodOrCtorDefaultReferences(AccessibleObject accobj, Class[] paramTypes) {
		PetiteReference[] lookupReferences = petiteConfig.getLookupReferences();
		MethodParameter[] methodParameters = null;
		if (petiteConfig.getUseParamo()) {
			methodParameters = Paramo.resolveParameters(accobj);
		}

		String[][] references = new String[paramTypes.length][];

		for (int j = 0; j < paramTypes.length; j++) {

			String[] ref = new String[lookupReferences.length];
			references[j] = ref;

			for (int i = 0; i < ref.length; i++) {
				switch (lookupReferences[i]) {
					case NAME:				ref[i] = methodParameters != null ? methodParameters[j].getName() : null; break;
					case TYPE_SHORT_NAME:	ref[i] = StringUtil.uncapitalize(paramTypes[j].getSimpleName()); break;
					case TYPE_FULL_NAME:	ref[i] = paramTypes[j].getName(); break;
				}
			}
		}

		return references;
	}

	protected void removeDuplicateNames(String[][] referencesArr) {
		for (String[] references : referencesArr) {
			removeDuplicateNames(references);
		}
	}

	/**
	 * Removes later duplicated references.
	 */
	protected void removeDuplicateNames(String[] references) {
		if (references.length < 2) {
			return;
		}

		for (int i = 1; i < references.length; i++) {
			String thisRef = references[i];
			if (thisRef == null) {
				continue;
			}

			for (int j = 0; j < i; j++) {
				if (references[j] == null) {
					continue;
				}
				if (thisRef.equals(references[j])) {
					references[i] = null;
					break;
				}
			}
		}
	}

}
