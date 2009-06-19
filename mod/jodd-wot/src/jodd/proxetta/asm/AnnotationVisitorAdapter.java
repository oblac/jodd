// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.AnnotationVisitor;

public class AnnotationVisitorAdapter implements AnnotationVisitor {

	protected final AnnotationVisitor dest;

	public AnnotationVisitorAdapter(AnnotationVisitor dest) {
		this.dest = dest;
	}

	public void visit(String s, Object o) {
		dest.visit(s, o);
	}

	public void visitEnum(String s, String s1, String s2) {
		dest.visitEnum(s, s1, s2);
	}

	public AnnotationVisitor visitAnnotation(String s, String s1) {
		dest.visitAnnotation(s, s1);
		return this;
	}

	public AnnotationVisitor visitArray(String s) {
		dest.visitArray(s);
		return this;
	}

	public void visitEnd() {
		dest.visitEnd();
	}

}
