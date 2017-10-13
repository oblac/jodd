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

import jodd.introspector.PropertyDescriptor;
import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.util.StringUtil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
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
			throw new PetiteException(
					"Different number of constructor parameters and references for: " + constructor.getName());
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
			throw new PetiteException("Different number of method parameters and references for: " +
					method.getDeclaringClass().getName() + '#' + method.getName());
		}
		removeDuplicateNames(references);
		return new MethodInjectionPoint(method, references);
	}

	/**
	 * Creates new property injection point.
	 */
	public PropertyInjectionPoint createPropertyInjectionPoint(PropertyDescriptor propertyDescriptor, String[] references) {
		if (references == null || references.length == 0) {
			references = fieldDefaultReferences(propertyDescriptor);
		}
		removeDuplicateNames(references);
		return new PropertyInjectionPoint(propertyDescriptor, references);
	}

	/**
	 * Creates new set injection point.
	 */
	public SetInjectionPoint createSetInjectionPoint(PropertyDescriptor propertyDescriptor) {
		return new SetInjectionPoint(propertyDescriptor);
	}

	// ---------------------------------------------------------------- utils

	/**
	 * Builds default field references.
	 */
	protected String[] fieldDefaultReferences(PropertyDescriptor propertyDescriptor) {
		PetiteReference[] lookupReferences = petiteConfig.getLookupReferences();
		String[] references = new String[lookupReferences.length];

		for (int i = 0; i < references.length; i++) {
			switch (lookupReferences[i]) {
				case NAME:				references[i] = propertyDescriptor.getName(); break;
				case TYPE_SHORT_NAME:	references[i] = StringUtil.uncapitalize(propertyDescriptor.getType().getSimpleName()); break;
				case TYPE_FULL_NAME:	references[i] = propertyDescriptor.getType().getName(); break;
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
