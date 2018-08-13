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

package jodd.petite.resolver;

import jodd.introspector.FieldDescriptor;
import jodd.introspector.MethodDescriptor;
import jodd.introspector.PropertyDescriptor;
import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.petite.PetiteConfig;
import jodd.petite.PetiteException;
import jodd.petite.PetiteReferenceType;
import jodd.petite.def.BeanReferences;
import jodd.petite.meta.PetiteInject;
import jodd.typeconverter.Converter;
import jodd.util.StringUtil;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

/**
 * Annotation reader for methods and constructors.
 */
public class ReferencesResolver {

	private final PetiteConfig petiteConfig;

	public ReferencesResolver(final PetiteConfig petiteConfig) {
		this.petiteConfig = petiteConfig;
	}

	/**
	 * Resolves reference from given values. Returns bean reference of given value or defaults
	 * if given name is blank.
	 */
	public BeanReferences resolveReferenceFromValue(final PropertyDescriptor propertyDescriptor, final String refName) {
		BeanReferences references;

		if (refName == null || refName.isEmpty()) {
			references = buildDefaultReference(propertyDescriptor);
		}
		else {
			references = BeanReferences.of(refName);
		}

		references = references.removeDuplicateNames();

		return references;
	}

	/**
	 * Takes given parameters references and returns reference set for given method or constructor.
	 */
	public BeanReferences[] resolveReferenceFromValues(final Executable methodOrCtor, final String... parameterReferences) {
		BeanReferences[] references = convertRefToReferences(parameterReferences);

		if (references == null || references.length == 0) {
			references = buildDefaultReferences(methodOrCtor);
		}

		if (methodOrCtor.getParameterTypes().length != references.length) {
			throw new PetiteException("Different number of method parameters and references for: " +
				methodOrCtor.getDeclaringClass().getName() + '#' + methodOrCtor.getName());
		}

		removeAllDuplicateNames(references);

		return references;
	}

	/**
	 * Extracts references for given property. Returns {@code null} if property is not marked with an
	 * annotation.
	 */
	public BeanReferences readReferenceFromAnnotation(final PropertyDescriptor propertyDescriptor) {
		final MethodDescriptor writeMethodDescriptor = propertyDescriptor.getWriteMethodDescriptor();
		final FieldDescriptor fieldDescriptor = propertyDescriptor.getFieldDescriptor();

		PetiteInject ref = null;
		if (writeMethodDescriptor != null) {
			ref = writeMethodDescriptor.getMethod().getAnnotation(PetiteInject.class);
		}
		if (ref == null && fieldDescriptor != null) {
			ref = fieldDescriptor.getField().getAnnotation(PetiteInject.class);
		}

		if (ref == null) {
			return null;
		}

		BeanReferences reference = null;

		String name = ref.value().trim();
		if (name.length() != 0) {
			reference = BeanReferences.of(name);
		}

		reference = updateReferencesWithDefaultsIfNeeded(propertyDescriptor, reference);

		reference = reference.removeDuplicateNames();

		return reference;
	}

	public BeanReferences readReferenceFromAnnotation(final FieldDescriptor fieldDescriptor) {
		final PetiteInject ref = fieldDescriptor.getField().getAnnotation(PetiteInject.class);

		if (ref == null) {
			return null;
		}

		BeanReferences reference = null;

		String name = ref.value().trim();
		if (name.length() != 0) {
			reference = BeanReferences.of(name);
		}

		//reference = updateReferencesWithDefaultsIfNeeded(propertyDescriptor, reference);

		reference = reference.removeDuplicateNames();

		return reference;
	}

	/**
	 * Extracts references from method or constructor annotation.
	 */
	public BeanReferences[] readAllReferencesFromAnnotation(final Executable methodOrCtor) {
		PetiteInject petiteInject = methodOrCtor.getAnnotation(PetiteInject.class);

		final Parameter[] parameters = methodOrCtor.getParameters();

		BeanReferences[] references;

		final boolean hasAnnotationOnMethodOrCtor;

		if (petiteInject != null) {
			references = convertAnnValueToReferences(petiteInject.value());

			hasAnnotationOnMethodOrCtor = true;
		}
		else {
			references = new BeanReferences[parameters.length];

			hasAnnotationOnMethodOrCtor = false;
		}

		int parametersWithAnnotationCount = 0;

		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];

			petiteInject = parameter.getAnnotation(PetiteInject.class);

			if (petiteInject == null) {
				// no annotation on argument
				continue;
			}

			// there is annotation on argument, override values
			String annotationValue = readAnnotationValue(petiteInject);

			if (annotationValue != null) {
				references[i] = BeanReferences.of(annotationValue);
			}

			parametersWithAnnotationCount++;
		}

		if (!hasAnnotationOnMethodOrCtor) {
			if (parametersWithAnnotationCount == 0) {
				return null;
			}
			if (parametersWithAnnotationCount != parameters.length) {
				throw new PetiteException("All arguments must be annotated with PetiteInject");
			}
		}

		references = updateReferencesWithDefaultsIfNeeded(methodOrCtor, references);

		removeAllDuplicateNames(references);

		return references;
	}

	/**
	 * Reads annotation value and returns {@code null} if value is empty.
	 */
	private String readAnnotationValue(final PetiteInject annotation) {
		String value = annotation.value().trim();

		if (value.isEmpty()) {
			return null;
		}
		return value;
	}

	private BeanReferences[] updateReferencesWithDefaultsIfNeeded(final Executable methodOrCtor, BeanReferences[] references) {
		BeanReferences[] defaultReferences = buildDefaultReferences(methodOrCtor);

		if (references == null || references.length == 0) {
			references = defaultReferences;
		}

		if (methodOrCtor.getParameterTypes().length != references.length) {
			throw new PetiteException(
				"Different number of parameters and references for: " + methodOrCtor.getName());
		}

		// apply default parameters
		for (int i = 0; i < references.length; i++) {
			BeanReferences parameterReferences = references[i];

			if (parameterReferenceIsNotSet(parameterReferences)) {
				references[i] = defaultReferences[i];
			}
		}

		return references;
	}

	private BeanReferences updateReferencesWithDefaultsIfNeeded(final PropertyDescriptor propertyDescriptor, BeanReferences references) {
		if (references == null || references.isEmpty()) {
			references = buildDefaultReference(propertyDescriptor);
		}

		return references;
	}

	/**
	 * Returns {@code true} if given parameter references is not set.
	 */
	private boolean parameterReferenceIsNotSet(final BeanReferences parameterReferences) {
		if (parameterReferences == null) {
			return true;
		}
		return parameterReferences.isEmpty();
	}

	/**
	 * Builds default method references.
	 */
	private BeanReferences[] buildDefaultReferences(final Executable methodOrCtor) {
		final boolean useParamo = petiteConfig.getUseParamo();
		final PetiteReferenceType[] lookupReferences = petiteConfig.getLookupReferences();
		MethodParameter[] methodParameters = null;

		if (useParamo) {
			methodParameters = Paramo.resolveParameters(methodOrCtor);
		}

		final Class[] paramTypes = methodOrCtor.getParameterTypes();
		final BeanReferences[] references = new BeanReferences[paramTypes.length];

		for (int j = 0; j < paramTypes.length; j++) {
			String[] ref = new String[lookupReferences.length];
			references[j] = BeanReferences.of(ref);

			for (int i = 0; i < ref.length; i++) {
				switch (lookupReferences[i]) {
					case NAME:
						ref[i] = methodParameters != null ? methodParameters[j].getName() : null;
						break;
					case TYPE_SHORT_NAME:
						ref[i] = StringUtil.uncapitalize(paramTypes[j].getSimpleName());
						break;
					case TYPE_FULL_NAME:
						ref[i] = paramTypes[j].getName();
						break;
				}
			}
		}

		return references;
	}

	/**
	 * Builds default field references.
	 */
	public BeanReferences buildDefaultReference(final PropertyDescriptor propertyDescriptor) {
		final PetiteReferenceType[] lookupReferences = petiteConfig.getLookupReferences();

		final String[] references = new String[lookupReferences.length];

		for (int i = 0; i < references.length; i++) {
			switch (lookupReferences[i]) {
				case NAME:
					references[i] = propertyDescriptor.getName();
					break;
				case TYPE_SHORT_NAME:
					references[i] = StringUtil.uncapitalize(propertyDescriptor.getType().getSimpleName());
					break;
				case TYPE_FULL_NAME:
					references[i] = propertyDescriptor.getType().getName();
					break;
			}
		}

		return BeanReferences.of(references);
	}

	/**
	 * Removes duplicate names from bean references.
	 */
	private void removeAllDuplicateNames(final BeanReferences[] allBeanReferences) {
		for (int i = 0; i < allBeanReferences.length; i++) {
			BeanReferences references = allBeanReferences[i];
			allBeanReferences[i] = references.removeDuplicateNames();
		}
	}


	/**
	 * Converts single string array to an array of bean references.
	 */
	private BeanReferences[] convertRefToReferences(final String[] references) {
		if (references == null) {
			return null;
		}
		BeanReferences[] ref = new BeanReferences[references.length];

		for (int i = 0; i < references.length; i++) {
			ref[i] = BeanReferences.of(references[i]);
		}
		return ref;
	}

	/**
	 * Converts comma-separated string into array of Bean references.
	 */
	private BeanReferences[] convertAnnValueToReferences(String value) {
		if (value == null) {
			return null;
		}

		value = value.trim();
		if (value.length() == 0) {
			return null;
		}

		String[] refNames = Converter.get().toStringArray(value);

		BeanReferences[] references = new BeanReferences[refNames.length];
		for (int i = 0; i < refNames.length; i++) {
			references[i] = BeanReferences.of(refNames[i].trim());
		}
		return references;
	}

}