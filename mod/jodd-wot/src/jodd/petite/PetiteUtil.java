// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.meta.PetiteBean;
import jodd.petite.scope.Scope;
import jodd.petite.scope.DefaultScope;
import jodd.util.StringUtil;

/**
 * Few Petite utilities, used internal.
 */
public class PetiteUtil {

	private static final String NAMES_DELIMITER = ", ";

	/**
	 * Resolves method or ctor parameter names either from annotation of from type names.
	 */
	public static String[] resolveParamReferences(String refValue, Class<?>[] paramTypes) {
		refValue = refValue.trim();
		String[] refNames;
		if (refValue.length() == 0) {
			refNames = resolveParamReferences(paramTypes);
		} else {
			refNames = StringUtil.splitc(refValue, NAMES_DELIMITER);
		}
		return refNames;
	}
	/**
	 * Resolves parameter names from type names.
	 */
	public static String[] resolveParamReferences(Class<?>[] paramTypes) {
		String[] refNames = new String[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			refNames[i] = StringUtil.uncapitalize(paramTypes[i].getSimpleName());
		}
		return refNames;
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
	public static String resolveBeanName(Class type) {
		PetiteBean petiteBean = ((Class<?>)type).getAnnotation(PetiteBean.class);
		String name = null;
		if (petiteBean != null) {
			name = petiteBean.value().trim();
		}
		if ((name == null) || (name.length() == 0)) {
			name = StringUtil.uncapitalize(type.getSimpleName());
		}
		return name;
	}

}
