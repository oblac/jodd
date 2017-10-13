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
s	 * Need to override to make java compiler happy.
	 */
	@Override
	public ActionAnnotationData<A> readAnnotationData(AccessibleObject accessibleObject) {
		return super.readAnnotationData(accessibleObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected ActionAnnotationData<A> createAnnotationData(A annotation) {

		ActionAnnotationData<A> ad = new ActionAnnotationData<>(annotation);

		ad.value = readString(annotation, "value", null);

		ad.extension = readString(annotation, "extension", null);

		ad.alias = readString(annotation, "alias", null);

		ad.method = readString(annotation, "method", null);

		ad.async = readBoolean(annotation, "async", false);

		ad.path = (Class<? extends ActionNamingStrategy>) readElement(annotation, "path");

		ad.result = (Class<? extends jodd.madvoc.result.ActionResult>) readElement(annotation, "result");

		return ad;
	}

}