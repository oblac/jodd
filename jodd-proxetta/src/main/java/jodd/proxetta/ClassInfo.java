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

package jodd.proxetta;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Various target class information.
 */
public interface ClassInfo {

	/**
	 * Returns package name.
	 */
	String getPackage();

	/**
	 * Returns simple class name.
	 */
	String getClassname();

	/**
	 * Returns super class reference. 
	 */
	String getSuperName();

	/**
	 * Returns class reference.
	 */
	String getReference();

	/**
	 * Returns array of super classes.
	 */
	public String[] getSuperClasses();

	/**
	 * Returns annotation information or <code>null</code> if target class has no annotations.
	 */
	AnnotationInfo[] getAnnotations();

	/**
	 * Returns a map of generic definitions. Keys are map names and values are
	 * raw types (after erasure).
	 */
	Map<String, String> getGenerics();

	// ---------------------------------------------------------------- annotations

	/**
	 * Finds annotation in class info. Returns <code>null</code> if annotation doesn't exist.
	 */
	default AnnotationInfo getAnnotation(Class<? extends Annotation> an) {
		AnnotationInfo[] anns = getAnnotations();
		if (anns == null) {
			return null;
		}
		String anName = an.getName();
		for (AnnotationInfo ann : anns) {
			if (ann.getAnnotationClassname().equals(anName)) {
				return ann;
			}
		}
		return null;
	}

	/**
	 * Returns <code>true</code> if class is annotated with one of provided annotation.
	 */
	default boolean hasAnnotation(Class<? extends Annotation>... an) {
		AnnotationInfo[] anns = getAnnotations();
		if (anns == null) {
			return false;
		}
		for (Class<? extends Annotation> annotationClass : an) {
			String anName = annotationClass.getName();
			for (AnnotationInfo ann : anns) {
				if (ann.getAnnotationClassname().equals(anName)) {
					return true;
				}
			}
		}
		return false;
	}

}