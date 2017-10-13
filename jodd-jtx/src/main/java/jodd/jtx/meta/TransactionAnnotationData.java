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
import jodd.util.AnnotationDataReader;

import java.lang.annotation.Annotation;

/**
 * {@link Transaction} annotation elements.
 */
public class TransactionAnnotationData<A extends Annotation> extends AnnotationDataReader.AnnotationData<A> {

	protected JtxPropagationBehavior propagation;
	protected JtxIsolationLevel isolation;
	protected boolean readOnly;
	protected int timeout;

	protected TransactionAnnotationData(A annotation) {
		super(annotation);
	}

	public JtxPropagationBehavior getPropagation() {
		return propagation;
	}

	public JtxIsolationLevel getIsolation() {
		return isolation;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public int getTimeout() {
		return timeout;
	}

}