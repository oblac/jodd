// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import jodd.util.AnnotationDataReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

/**
 * Action method annotation reader.
 */
public class ActionAnnotation<A extends Annotation> extends AnnotationDataReader<A, ActionAnnotationData<A>> {

	public ActionAnnotation() {
	}

	public ActionAnnotation(Class<A> annotationClass) {
		super(annotationClass);
	}

	/**
	 * Need to override to make java compiler happy.
	 */
	@Override
	public ActionAnnotationData<A> readAnnotationData(AccessibleObject accessibleObject) {
		return super.readAnnotationData(accessibleObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ActionAnnotationData<A> createAnnotationData(A annotation) {

		ActionAnnotationData<A> ad = new ActionAnnotationData<A>(annotation);

		ad.value = readStringElement(annotation, "value");

		ad.extension = readStringElement(annotation, "extension");

		ad.alias = readStringElement(annotation, "alias");

		ad.method = readStringElement(annotation, "method");

		return ad;
	}

}
