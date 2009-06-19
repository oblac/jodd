// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.proxetta.AnnotationData;

/**
 * Reads annotation inner data.
 */
class AnnotationReader extends EmptyAnnotationVisitor {

	final AnnotationData annData;

	AnnotationReader(AnnotationData annData) {
		this.annData = annData;
	}

	@Override
	public void visit(String name, Object value) {
		annData.values.put(name, value);
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		System.out.println("cdcccccccccccccccc " + name  + desc + value);		
	}

}
