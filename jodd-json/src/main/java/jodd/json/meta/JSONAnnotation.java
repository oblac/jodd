// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.meta;

import jodd.util.AnnotationDataReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

/**
 * JSON Annotation reader.
 */
public class JSONAnnotation<A extends Annotation> extends AnnotationDataReader<A, JSONAnnotationData<A>> {

	public JSONAnnotation(Class<A> annotationClass) {
		super(annotationClass, JSON.class);
	}

	/**
	 * Need to override to make java compiler happy.
	 */
	@Override
	public JSONAnnotationData<A> readAnnotationData(AccessibleObject accessibleObject) {
		return super.readAnnotationData(accessibleObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JSONAnnotationData<A> createAnnotationData(A annotation) {

		JSONAnnotationData<A> jad = new JSONAnnotationData<A>(annotation);

		jad.name = readString(annotation, "name", null);
		jad.included = readBoolean(annotation, "include", true);
		jad.strict = readBoolean(annotation, "strict", false);

		return jad;
	}

}