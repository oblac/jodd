// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.meta;

import jodd.madvoc.path.ActionNamingStrategy;
import jodd.util.AnnotationDataReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

/**
 * Action method annotation reader.
 */
public class ActionAnnotation<A extends Annotation> extends AnnotationDataReader<A, ActionAnnotationData<A>> {

	public ActionAnnotation(Class<A> annotationClass) {
		super(annotationClass, Action.class);
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

		ad.value = readString(annotation, "value", null);

		ad.extension = readString(annotation, "extension", null);

		ad.alias = readString(annotation, "alias", null);

		ad.method = readString(annotation, "method", null);

		ad.async = readBoolean(annotation, "async", false);

		ad.path = (Class<? extends ActionNamingStrategy>) readElement(annotation, "path");

		return ad;
	}


}