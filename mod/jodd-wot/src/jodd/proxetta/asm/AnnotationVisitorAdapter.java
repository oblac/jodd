// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import org.objectweb.asm.AnnotationVisitor;

public class AnnotationVisitorAdapter extends EmptyAnnotationVisitor {

	protected final AnnotationVisitor dest;

	public AnnotationVisitorAdapter(AnnotationVisitor dest) {
		this.dest = dest;
	}

	@Override
	public void visit(String name, Object value) {
		dest.visit(name, value);
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		dest.visitEnum(name, desc, value);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		dest.visitAnnotation(name, desc);
		return this;
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		dest.visitArray(name);
		return this;
	}

	@Override
	public void visitEnd() {
		dest.visitEnd();
	}

}
