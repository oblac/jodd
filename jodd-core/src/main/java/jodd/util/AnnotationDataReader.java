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

package jodd.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

/**
 * Annotation reader reads an annotation and returns {@link AnnotationData annotation data object}
 * populated with annotation element values. Can be used to simulate annotation inheritance,
 * as such does not exist in Java.
 * <p>
 * There are 3 ways how this class can be used. First, it can be used on single annotation,
 * but that does not make much sense.
 * <p>
 * Second way is with child and parent annotation. The parent annotation is default one,
 * like a base class. Child annotation contains some predefined values different from parent.
 * Note that child annotation does NOT have to specify all elements - all missing elements
 * will be read from default parent annotation. So child annotation behaves like it is
 * overriding the parent one.
 * <p>
 * Third way is similar, except the child annotation is also annotated with parent annotation!
 * Besides overriding features and default values, this way we can finalize some element value
 * and prevent it from being modified by user.
 */
public abstract class AnnotationDataReader<A extends Annotation, D extends AnnotationDataReader.AnnotationData<A>> {

	protected final Annotation defaultAnnotation;
	protected final Class<A> annotationClass;

	// ---------------------------------------------------------------- ctor

	/**
	 * Creates new annotation data reader using annotation definition
	 * from class generics. Moreover, allows annotation to be annotated
	 * with default annotation, for convenient and fail-back value reading.
	 * @param annotationClass annotation type to read from
	 * @param defaultAnnotationClass optional default annotation type, used to annotate the annotation class.
	 */
	@SuppressWarnings( {"unchecked"})
	protected AnnotationDataReader(Class<A> annotationClass, Class<? extends Annotation> defaultAnnotationClass) {
		if (annotationClass == null) {
			Class[] genericSupertypes = ClassUtil.getGenericSupertypes(this.getClass());

			if (genericSupertypes != null) {
				annotationClass = genericSupertypes[0];
			}

			if (annotationClass == null || annotationClass == Annotation.class) {
				throw new IllegalArgumentException("Missing annotation from generics supertype");
			}
		}
		this.annotationClass = annotationClass;

		// read default annotation
		if (defaultAnnotationClass != null && defaultAnnotationClass != annotationClass) {

			Annotation defaultAnnotation = annotationClass.getAnnotation(defaultAnnotationClass);

			// no default annotation on parent, create annotation
			if (defaultAnnotation == null) {
				try {
					defaultAnnotation = defaultAnnotationClass.newInstance();
				} catch (Exception ignore) {
				}
			}

			this.defaultAnnotation = defaultAnnotation;
		} else {
			this.defaultAnnotation = null;
		}
	}

	// ---------------------------------------------------------------- methods

	/**
	 * Returns annotation class.
	 */
	public Class<A> getAnnotationClass() {
		return annotationClass;
	}

	/**
	 * Returns <code>true</code> if annotation is present on
	 * given accessible object.
	 */
	public boolean hasAnnotation(AccessibleObject accessibleObject) {
		return accessibleObject.isAnnotationPresent(annotationClass);
	}

	/**
	 * Reads {@link AnnotationData annotation data} on provided accessible object.
	 * If annotation is not presented, <code>null</code> is returned.
	 */
	public D readAnnotationData(AccessibleObject accessibleObject) {
		A annotation = accessibleObject.getAnnotation(annotationClass);
		if (annotation == null) {
			return null;
		}

		return createAnnotationData(annotation);
	}

	/**
	 * Reads {@link AnnotationData annotation data} on provided type.
	 * If annotation is not presented, <code>null</code> is returned.
	 */
	public D readAnnotationData(Class<?> type) {
		A annotation = type.getAnnotation(annotationClass);
		if (annotation == null) {
			return null;
		}

		return createAnnotationData(annotation);
	}

	/**
	 * Creates annotation data from given annotation.
	 */
	protected abstract D createAnnotationData(A annotation);

	// ---------------------------------------------------------------- read

	/**
	 * Reads non-empty, trimmed, annotation element value. If annotation value is
	 * missing, it will read value from default annotation. If still missing,
	 * returns <code>null</code>.
	 */
	protected String readStringElement(A annotation, String name) {
		Object annotationValue = ClassUtil.readAnnotationValue(annotation, name);
		if (annotationValue == null) {
			if (defaultAnnotation == null) {
				return null;
			}
			annotationValue = ClassUtil.readAnnotationValue(defaultAnnotation, name);
			if (annotationValue == null) {
				return null;
			}
		}
		String value = StringUtil.toSafeString(annotationValue);
		return value.trim();
	}

	/**
	 * Reads annotation element as an object. If annotation value
	 * is missing, it will be read from default annotation.
	 * If still missing, returns <code>null</code>.
	 */
	protected Object readElement(A annotation, String name) {
		Object annotationValue = ClassUtil.readAnnotationValue(annotation, name);
		if (annotationValue == null) {
			if (defaultAnnotation != null) {
				annotationValue = ClassUtil.readAnnotationValue(defaultAnnotation, name);
			}
		}
		return annotationValue;
	}


	/**
	 * Reads string element from the annotation. Empty strings are detected
	 * and default value is returned instead.
	 */
	protected String readString(A annotation, String name, String defaultValue) {
		String value = readStringElement(annotation, name);

		if (StringUtil.isEmpty(value)) {
			value = defaultValue;
		}

		return value;
	}


	/**
	 * Reads boolean element from the annotation.
	 */
	protected boolean readBoolean(A annotation, String name, boolean defaultValue) {
		Boolean value = (Boolean) readElement(annotation, name);
		if (value == null) {
			return defaultValue;
		}
		return value.booleanValue();
	}


	/**
	 * Reads int element from the annotation.
	 */
	protected int readInt(A annotation, String name, int defaultValue) {
		Integer value = (Integer) readElement(annotation, name);
		if (value == null) {
			return defaultValue;
		}
		return value.intValue();
	}

	// ---------------------------------------------------------------- annotation data

	/**
	 * Base class for annotation data, for holding annotation elements values.
	 */
	public abstract static class AnnotationData<N extends Annotation> {

		protected final N annotation;

		protected AnnotationData(N annotation) {
			this.annotation = annotation;
		}

		/**
		 * Returns annotation.
		 */
		public N getAnnotation() {
			return annotation;
		}
	}

}