// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.petite;

import jodd.petite.meta.PetiteBean;
import jodd.petite.scope.Scope;
import jodd.typeconverter.Converter;
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

		String[] refNames = Converter.get().toStringArray(value);

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

	/**
	 * Returns <code>true</code> if bean has name defined by Petite annotation.
	 */
	public static boolean beanHasAnnotationName(Class type) {
		PetiteBean petiteBean = ((Class<?>)type).getAnnotation(PetiteBean.class);

		if (petiteBean == null) {
			return false;
		}

		String name = petiteBean.value().trim();

		return !name.isEmpty();
	}

}
