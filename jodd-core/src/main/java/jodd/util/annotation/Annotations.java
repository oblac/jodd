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

package jodd.util.annotation;

import jodd.bridge.Packages;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Annotations collector.
 */
public class Annotations<A extends Annotation> {
	private final Class<A> annotationClass;
	private final List<A> annotations = new ArrayList<>();

	public Annotations(final Class<A> annotationClass) {
		this.annotationClass = annotationClass;
	}

	public static <T extends Annotation> Annotations<T> of(final Class<T> annotationClass) {
		return new Annotations<>(annotationClass);
	}

	public Annotations onMethod(final Method method) {
		A a = method.getAnnotation(annotationClass);

		if (a != null) {
			annotations.add(a);
		}
		return this;
	}

	public Annotations<A> onClass(final Class type) {
		A a = (A) type.getAnnotation(annotationClass);

		if (a != null) {
			annotations.add(a);
		}
		return this;
	}

	public Annotations<A> onPackageHierarchyOf(final Class type) {
		Package pck = type.getPackage();
		String packageName = pck.getName();

		while (true) {
			if (pck != null) {
				A a = pck.getAnnotation(annotationClass);

				if (a != null) {
					annotations.add(a);
				}
			}

			int ndx = packageName.lastIndexOf('.');

			if (ndx == -1) {
				break;
			}

			packageName = packageName.substring(0, ndx);

			pck = Packages.of(type.getClassLoader(), packageName);
		}

		return this;
	}

	/**
	 * Returns collected annotations.
	 */
	public List<A> collect() {
		return annotations;
	}
}
