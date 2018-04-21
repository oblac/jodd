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

package jodd.jtx.meta;

import jodd.jtx.JtxIsolationLevel;
import jodd.jtx.JtxPropagationBehavior;
import jodd.jtx.JtxTransactionMode;
import jodd.util.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class TransactionAnnotationValues implements Transaction {

	/**
	 * Shortcut methods for given annotation class.
	 */
	public static AnnotationParser parserFor(final Class<? extends Annotation> annotationClass) {
		return new AnnotationParser(annotationClass, Transaction.class);
	}

	/**
	 * Shortcut for checking the annotation on annotated element and returning either the values or {@code null}.
	 */
	public static TransactionAnnotationValues of(final AnnotationParser annotationParser, final AnnotatedElement annotatedElement) {
		if (!annotationParser.hasAnnotationOn(annotatedElement)) {
			return null;
		}
		return new TransactionAnnotationValues(annotationParser.of(annotatedElement));
	}

	protected final Class<? extends Annotation> annotationType;
	protected final JtxPropagationBehavior propagation;
	protected final JtxIsolationLevel isolation;
	protected final boolean readOnly;
	protected final int timeout;

	private TransactionAnnotationValues(final AnnotationParser.Reader reader) {
		this.annotationType = reader.annotationType();

		this.propagation = (JtxPropagationBehavior) reader.readElement("propagation");

		this.isolation = (JtxIsolationLevel) reader.readElement("isolation");

		this.readOnly = reader.readBoolean("readOnly", false);

		this.timeout = reader.readInt("timeout", JtxTransactionMode.DEFAULT_TIMEOUT);
	}


	@Override
	public JtxPropagationBehavior propagation() {
		return propagation;
	}

	@Override
	public JtxIsolationLevel isolation() {
		return isolation;
	}

	@Override
	public boolean readOnly() {
		return readOnly;
	}

	@Override
	public int timeout() {
		return timeout;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return null;
	}
}
