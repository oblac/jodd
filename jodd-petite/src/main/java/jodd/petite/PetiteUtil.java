// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.petite;

import jodd.petite.meta.PetiteBean;
import jodd.petite.scope.Scope;
import jodd.typeconverter.Convert;
import jodd.util.StringUtil;

import java.lang.reflect.Constructor;

/**
 * Few Petite utilities, used internally.
 */
public class PetiteUtil {

	/**
	 * Creates new instance of given type. In the first try, it tries to use
	 * constructor with a {@link PetiteContainer}. If that files, uses default
	 * constructor to builds an instance.
	 */
	public static <T> T newInstance(Class<T> type, PetiteContainer petiteContainer) throws Exception {
		T t = null;

		// first try ctor(PetiteContainer)
		try {
			Constructor<T> ctor = type.getConstructor(PetiteContainer.class);
			t = ctor.newInstance(petiteContainer);
		} catch (NoSuchMethodException nsmex) {
			// ignore
		}

		// if first try failed, try default ctor
		if (t == null) {
			return type.newInstance();
		}

		return t;
	}

	/**
	 * Calls destroy methods on given BeanData. Destroy methods are called
	 * without any order.
	 */
	public static void callDestroyMethods(BeanData beanData) {
		DestroyMethodPoint[] dmp = beanData.getBeanDefinition().getDestroyMethodPoints();
		for (DestroyMethodPoint destroyMethodPoint : dmp) {
			try {
				destroyMethodPoint.method.invoke(beanData.getBean());
			} catch (Exception ex) {
				throw new PetiteException("Invalid destroy method: " + destroyMethodPoint.method, ex);
			}
		}
	}

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
	 * Resolves bean's scope type from the annotation. Returns <code>null</code>
	 * if annotation doesn't exist.
	 */
	public static Class<? extends Scope> resolveBeanScopeType(Class type) {
		PetiteBean petiteBean = ((Class<?>) type).getAnnotation(PetiteBean.class);
		return petiteBean != null ? petiteBean.scope() : null;
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
