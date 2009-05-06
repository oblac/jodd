// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.AnnotationVisitor;
import jodd.proxetta.AnnotationData;

/**
 * Reads annotation inner data.
 */
class AnnotationReader implements AnnotationVisitor {

	final AnnotationData annData;

	AnnotationReader(AnnotationData annData) {
		this.annData = annData;
	}

	public void visit(String string, Object object) {
		annData.values.put(string, object);
	}

	public void visitEnum(String string, String string1, String string2) {
	}

	public AnnotationVisitor visitAnnotation(String string, String string1) {
		return this;
	}

	public AnnotationVisitor visitArray(String string) {
		return this;
	}

	public void visitEnd() {
	}
}
