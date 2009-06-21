// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.MethodInfo;

import java.lang.annotation.Annotation;

/**
 * Pointcut on method with given annotation.
 */
public class MethodAnnotationPointcut extends ProxyPointcutSupport {

	protected final Class<? extends Annotation> annotationClass;

	public MethodAnnotationPointcut(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	public boolean apply(MethodInfo methodInfo) {
		return lookupAnnotation(methodInfo, annotationClass) != null;
	}
}
