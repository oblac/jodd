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

	/**
	 * Visits a primitive value of the annotation.
	 */
	public void visit(String name, Object value) {
		annData.values.put(name, value);
	}

	/**
	 * Visits an enumeration value of the annotation.
	 */
	public void visitEnum(String name, String desc, String value) {
		System.out.println("cdcccccccccccccccc " + name  + desc + value);		
	}

	/**
	 * Visits a nested annotation value of the annotation.
	 */
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		return this;
	}

	/**
	 * Visits an array value of the annotation.
	 */
	public AnnotationVisitor visitArray(String name) {
		return this;
	}

	public void visitEnd() {
	}
}
