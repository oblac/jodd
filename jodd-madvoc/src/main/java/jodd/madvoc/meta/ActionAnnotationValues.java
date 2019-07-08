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

import jodd.util.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Action values
 */
public class ActionAnnotationValues implements Action {

	public static ActionAnnotationValues of(final AnnotationParser annotationParser, final AnnotatedElement annotatedElement) {
		if (!annotationParser.hasAnnotationOn(annotatedElement)) {
			return null;
		}
		return new ActionAnnotationValues(annotationParser.of(annotatedElement));
	}

	protected final String value;
	protected final String alias;
	protected final Class<? extends Annotation> annotationType;

	private ActionAnnotationValues(final AnnotationParser.Reader reader) {
		this.annotationType = reader.annotationType();

		String rawValue = reader.readString("value", null);
		String rawAlias = null;

		if (rawValue != null) {
			final int ndx = rawValue.indexOf("<");
			if (ndx != -1) {
				rawAlias = rawValue.substring(ndx + 1, rawValue.length() - 1);
				rawValue = rawValue.substring(0, ndx - 1);
			}
		}

		this.value = rawValue;
		this.alias = reader.readString("alias", rawAlias);
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public String alias() {
		return alias;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return annotationType;
	}
}
