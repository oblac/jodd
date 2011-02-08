// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.util;

import jodd.typeconverter.Convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

/**
 * Annotation reader that reads an annotation and returns annotation data object filled
 * with annotation element values. May be used when having two or more annotations with
 * same elements - since java doesn't support annotation inheritance.
 */
public abstract class AnnotationDataReader<A extends Annotation, D extends AnnotationDataReader.AnnotationData<A>> {

	protected final Class<A> annotationClass;

	// ---------------------------------------------------------------- ctor

	/**
	 * Creates new annotation data reader using annotation definition
	 * from class generics.
	 */
	@SuppressWarnings( {"unchecked"})
	protected AnnotationDataReader() {
		annotationClass = ReflectUtil.getGenericSupertype(this.getClass());
		if (annotationClass == null) {
			throw new IllegalArgumentException("Unable to resolve annotation from generic supertype.");
		}
	}

	/**
	 * Creates new annotation data reader using provided annotation class.
	 */
	protected AnnotationDataReader(Class<A> annotationClass) {
		this.annotationClass = annotationClass;
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
	 * Creates annotation data from given annotation.
	 */
	protected abstract D createAnnotationData(A annotation);

	// ---------------------------------------------------------------- read

	/**
	 * Reads non-empty, trimmed, annotation element value. If annotation value is
	 * an empty string, returns <code>null</code>.
	 */
	protected String readStringElement(A annotation, String name) {
		String value = Convert.toString(ReflectUtil.readAnnotationValue(annotation, name));
		if (value != null) {
			value = value.trim();
			if (value.length() == 0) {
				value = null;
			}
		}
		return value;
	}

	/**
	 * Reads annotation element as an object.
	 */
	protected Object readElement(A annotation, String name) {
		return ReflectUtil.readAnnotationValue(annotation, name);
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
