// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.MethodInfo;

import java.lang.annotation.Annotation;

/**
 * Pointcut on method with one of given annotations.
 */
public class MethodAnnotationPointcut extends ProxyPointcutSupport {

	protected final Class<? extends Annotation>[] annotationClasses;

	/**
	 * Defines set of annotations we are looking for.
	 */
	public MethodAnnotationPointcut(Class<? extends Annotation>... annotationClasses) {
		this.annotationClasses = annotationClasses;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean apply(MethodInfo methodInfo) {
		return hasAnnotation(methodInfo, annotationClasses);
	}
}
