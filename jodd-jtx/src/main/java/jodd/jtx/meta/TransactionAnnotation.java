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
import jodd.util.AnnotationDataReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

/**
 * Transaction annotation.
 */
public class TransactionAnnotation<A extends Annotation> extends AnnotationDataReader<A, TransactionAnnotationData<A>> {

	public TransactionAnnotation(Class<A> annotationClass) {
		super(annotationClass, Transaction.class);
	}

	/**
	 * Need to override to make java compiler happy.
	 */
	@Override
	public TransactionAnnotationData<A> readAnnotationData(AccessibleObject accessibleObject) {
		return super.readAnnotationData(accessibleObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TransactionAnnotationData<A> createAnnotationData(A annotation) {

		TransactionAnnotationData<A> td = new TransactionAnnotationData<>(annotation);

		td.propagation = (JtxPropagationBehavior) readElement(annotation, "propagation");

		td.isolation = (JtxIsolationLevel) readElement(annotation, "isolation");

		td.readOnly = readBoolean(annotation, "readOnly", false);

		td.timeout = readInt(annotation, "timeout", JtxTransactionMode.DEFAULT_TIMEOUT);

		return td;
	}

}
