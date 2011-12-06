// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.asm;

import org.objectweb.asm.AnnotationVisitor;

/**
 * Annotation visitor adapter.
 */
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
		return new AnnotationVisitorAdapter(dest.visitAnnotation(name, desc));
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		return new AnnotationVisitorAdapter(dest.visitArray(name));
	}

	@Override
	public void visitEnd() {
		dest.visitEnd();
	}

}
