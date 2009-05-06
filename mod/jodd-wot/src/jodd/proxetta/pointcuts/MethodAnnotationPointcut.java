// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.pointcuts;

import jodd.proxetta.MethodSignature;
import jodd.proxetta.ProxettaException;

/**
 * Pointcut on method with given annotation.
 */
public class MethodAnnotationPointcut extends ProxyPointcutSupport {

	protected final String annotationName;

	public MethodAnnotationPointcut(Class annotationClass) {
		if (annotationClass.isAnnotation() == false) {
			throw new ProxettaException(this.getClass().getSimpleName() + " works only with annotation classes: " + annotationClass.getName());
		}
		this.annotationName = annotationClass.getName();
	}

	public boolean apply(MethodSignature msign) {
		return hasAnnotation(msign, annotationName);
	}
}
