// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

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