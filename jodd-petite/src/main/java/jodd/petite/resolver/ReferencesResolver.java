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

import jodd.paramo.MethodParameter;
import jodd.paramo.Paramo;
import jodd.petite.PetiteException;
import jodd.petite.PetiteReference;
import jodd.petite.PetiteUtil;
import jodd.petite.meta.PetiteInject;
import jodd.util.StringUtil;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

/**
 * Annotation reader for methods and constructors.
 * todo remove petite utils here.
 */
public class ReferencesResolver {

	private final PetiteReference[] lookupReferences;
	private final boolean useParamo;

	/**
	 * Resolves references.
	 */
	public ReferencesResolver(PetiteReference[] lookupReferences, boolean useParamo) {
		this.lookupReferences = lookupReferences;
		this.useParamo = useParamo;
	}


	/**
	 * Takes given parameters references and returns reference set for given method or constructor.
	 */
	public String[][] resolveReferenceFromValues(Executable methodOrCtor, String... parameterReferences) {
		String[][] references = PetiteUtil.convertRefToReferences(parameterReferences);

		if (references == null || references.length == 0) {
			references = buildDefaultReferences(methodOrCtor);
		}

		if (methodOrCtor.getParameterTypes().length != references.length) {
			throw new PetiteException("Different number of method parameters and references for: " +
				methodOrCtor.getDeclaringClass().getName() + '#' + methodOrCtor.getName());
		}

		removeDuplicateNames(references);

		return references;
	}

	/**
	 * Extracts references from method or constructor annotation.
	 * todo napravi klasu BeanReference
	 */
	public String[][] readReferencesFromAnnotation(Executable methodOrCtor) {

		PetiteInject petiteInject = methodOrCtor.getAnnotation(PetiteInject.class);

		final Parameter[] parameters = methodOrCtor.getParameters();

		String[][] references;

		final boolean hasAnnotationOnMethodOrCtor;

		if (petiteInject != null) {
			references = PetiteUtil.convertAnnValueToReferences(petiteInject.value());

			references = updateReferencesWithDefaultsIfNeeded(methodOrCtor, references);

			hasAnnotationOnMethodOrCtor = true;
		}
		else {
			references = new String[parameters.length][];

			hasAnnotationOnMethodOrCtor = false;
		}

		int parametersWtihAnnotationCount = 0;

		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];

			petiteInject = parameter.getAnnotation(PetiteInject.class);

			if (petiteInject == null) {
				// no annotation on argument
				continue;
			}

			// there is annotation on argument, override values
			references[i] = new String[] {readAnnotationValue(petiteInject)};

			parametersWtihAnnotationCount++;
		}

		if (!hasAnnotationOnMethodOrCtor) {
			if (parametersWtihAnnotationCount == 0) {
				return null;
			}
			if (parametersWtihAnnotationCount != parameters.length) {
				throw new PetiteException("All arguments must be annotated with PetiteInject");
			}
		}

		references = updateReferencesWithDefaultsIfNeeded(methodOrCtor, references);

		return references;
	}

	/**
	 * Reads annotation value and returns {@code null} if value is empty.
	 */
	private String readAnnotationValue(PetiteInject annotation) {
		String value = annotation.value().trim();

		if (value.isEmpty()) {
			return null;
		}
		return value;
	}

	private String[][] updateReferencesWithDefaultsIfNeeded(Executable methodOrCtor, String[][] references) {
		String[][] defaultReferences = buildDefaultReferences(methodOrCtor);

		if (references == null || references.length == 0) {
			references = defaultReferences;
		}

		if (methodOrCtor.getParameterTypes().length != references.length) {
			throw new PetiteException(
				"Different number of parameters and references for: " + methodOrCtor.getName());
		}

		// apply default parameters
		for (int i = 0; i < references.length; i++) {
			String[] parameterReferences = references[i];

			if (parameterReferenceIsNotSet(parameterReferences)) {
				references[i] = defaultReferences[i];
			}
		}

		removeDuplicateNames(references);

		return references;
	}

	/**
	 * Returns {@code true} if given parameter references is not set.
	 */
	private boolean parameterReferenceIsNotSet(String[] parameterReferences) {
		if (parameterReferences == null) {
			return true;
		}
		if ((parameterReferences.length == 1) && (parameterReferences[0] == null)) {
			return true;
		}
		return false;
	}

	/**
	 * Builds default method references.
	 */
	private String[][] buildDefaultReferences(Executable methodOrCtor) {
		MethodParameter[] methodParameters = null;

		if (useParamo) {
			methodParameters = Paramo.resolveParameters(methodOrCtor);
		}

		final Class[] paramTypes = methodOrCtor.getParameterTypes();
		final String[][] references = new String[paramTypes.length][];

		for (int j = 0; j < paramTypes.length; j++) {
			String[] ref = new String[lookupReferences.length];
			references[j] = ref;

			for (int i = 0; i < ref.length; i++) {
				switch (lookupReferences[i]) {
					case NAME:				ref[i] = methodParameters != null ? methodParameters[j].getName() : null;
											break;
					case TYPE_SHORT_NAME:	ref[i] = StringUtil.uncapitalize(paramTypes[j].getSimpleName());
											break;
					case TYPE_FULL_NAME:	ref[i] = paramTypes[j].getName();
											break;
				}
			}
		}

		return references;
	}

	/**
	 * Removes duplicate names.
	 */
	private void removeDuplicateNames(String[][] referencesArr) {
		for (String[] references : referencesArr) {
			removeDuplicateNames(references);
		}
	}

	/**
	 * Removes later duplicated references in an array by setting duplicates to {@code null}.
	 */
	private void removeDuplicateNames(String[] references) {
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