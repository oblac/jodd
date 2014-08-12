// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.meta;

import jodd.util.AnnotationDataReader;

import java.lang.annotation.Annotation;

/**
 * Holder for JSON annotation data.
 */
public class JSONAnnotationData<A extends Annotation> extends AnnotationDataReader.AnnotationData<A> {

	protected boolean included;

	protected JSONAnnotationData(A annotation) {
		super(annotation);
	}

	public boolean isIncluded() {
		return included;
	}
}