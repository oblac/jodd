// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.meta.PetiteBean;
import jodd.petite.scope.Scope;
import jodd.petite.scope.DefaultScope;
import jodd.typeconverter.Convert;
import jodd.util.StringUtil;

/**
 * Few Petite utilities, used internally.
 */
public class PetiteUtil {

	/**
	 * Converts comma-separated string into double string array.
	 */
	public static String[][] convertAnnValueToReferences(String value) {
		if (value == null) {
			return null;
		}
		value = value.trim();
		if (value.length() == 0) {
			return null;
		}

		String[] refNames = Convert.toStringArray(value);

		// convert to double str array
		String[][] references = new String[refNames.length][];
		for (int i = 0; i < refNames.length; i++) {
			references[i] = new String[] {refNames[i].trim()};
		}
		return references;
	}

	/**
	 * Converts single string array to double string array.
	 */
	public static String[][] convertRefToReferences(String[] references) {
		if (references == null) {
			return null;
		}
		String[][] ref = new String[references.length][];
		for (int i = 0; i < references.length; i++) {
			ref[i] = new String[] {references[i]};
		}
		return ref;
	}

	/**
	 * Resolves bean's auto-wire flag from the annotation. Returns default auto-wire if annotation doesn't exist.
	 */
	public static WiringMode resolveBeanWiringMode(Class type) {
		PetiteBean petiteBean = ((Class<?>) type).getAnnotation(PetiteBean.class);
		return petiteBean != null ? petiteBean.wiring() : WiringMode.DEFAULT;
	}

	/**
	 * Resolves bean's scope type from the annotation. Returns default scope if annotation doesn't exist.
	 */
	public static Class<? extends Scope> resolveBeanScopeType(Class type) {
		PetiteBean petiteBean = ((Class<?>) type).getAnnotation(PetiteBean.class);
		return petiteBean != null ? petiteBean.scope() : DefaultScope.class;
	}

	/**
	 * Resolves bean's name from bean annotation or type name. May be used for resolving bean name
	 * of base type during registration of bean subclass.
	 */
	public static String resolveBeanName(Class type, boolean useLongTypeName) {
		PetiteBean petiteBean = ((Class<?>)type).getAnnotation(PetiteBean.class);
		String name = null;
		if (petiteBean != null) {
			name = petiteBean.value().trim();
		}
		if ((name == null) || (name.length() == 0)) {
			if (useLongTypeName) {
				name = type.getName();
			} else {
				name = StringUtil.uncapitalize(type.getSimpleName());
			}
		}
		return name;
	}

}
